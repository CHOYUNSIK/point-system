package com.musinsa.point.service;

import com.musinsa.point.config.annotation.OptimisticRetry;
import com.musinsa.point.entity.PointTransaction;
import com.musinsa.point.entity.enums.PointTransactionType;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointTransactionRepository;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointEarnResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import com.musinsa.point.service.dto.PointUseCancelCommand;
import com.musinsa.point.service.dto.PointUseCancelResult;
import com.musinsa.point.service.dto.PointUseCancelResult.PointUseCancelResultBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointTransactionService {

    @Value("${point.default-expiration-days}")
    private int defaultExpirationDays;

    private final PointService pointService;

    private final PointTransactionRepository pointTransactionRepository;

    @OptimisticRetry
    @Transactional
    public List<PointUseCancelResult> cancelUsedPoint(PointUseCancelCommand command) {
        validateCancelAmount(command.cancelAmount());

        List<PointTransaction> transactionList = getPointTransactions(command.orderId());

        long totalCancelableAmount = calculateTotalCancelableAmount(transactionList);

        validateCancelableAmount(command.cancelAmount(), totalCancelableAmount);

        return processPointCancellation(transactionList, command.cancelAmount());
    }

    private void validateCancelAmount(long cancelAmount) {
        if (cancelAmount <= 0) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "취소 금액은 1원 이상이어야 합니다.");
        }
    }

    private List<PointTransaction> getPointTransactions(Long orderId) {
        List<PointTransaction> transactionList = pointTransactionRepository.findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(
            orderId, PointTransactionType.USE);

        if (transactionList.isEmpty()) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "해당 포인트 사용 내역을 찾을 수 없습니다.");
        }

        return transactionList;
    }

    private long calculateTotalCancelableAmount(List<PointTransaction> transactionList) {
        return transactionList.stream()
                              .mapToLong(this::getRemainingCancelableAmount)
                              .sum();
    }

    private long getRemainingCancelableAmount(PointTransaction transaction) {
        long alreadyCanceled = pointTransactionRepository
            .findByOriginalTransactionIdAndTransactionType(transaction.getId(), PointTransactionType.CANCEL)
            .stream()
            .mapToLong(PointTransaction::getUsedAmount)
            .sum();

        return transaction.getUsedAmount() - alreadyCanceled;
    }

    private void validateCancelableAmount(long cancelAmount, long totalCancelableAmount) {
        if (cancelAmount > totalCancelableAmount) {
            throw new GeneralException(ErrorCode.BAD_REQUEST,
                "취소 가능한 금액을 초과하여 사용할 수 없습니다. (취소 가능 금액: " + totalCancelableAmount + ")");
        }
    }

    private List<PointUseCancelResult> processPointCancellation(List<PointTransaction> transactionList, long cancelAmount) {
        List<PointUseCancelResult> results = new ArrayList<>();
        long remainingCancelAmount = cancelAmount;

        for (PointTransaction transaction : transactionList) {
            if (remainingCancelAmount == 0) {
                break;
            }

            long refundAmount = Math.min(getRemainingCancelableAmount(transaction), remainingCancelAmount);
            if (refundAmount <= 0) {
                continue;
            }

            remainingCancelAmount -= refundAmount;

            PointTransaction cancelTransaction = PointTransaction.createUseCancelTransaction(transaction, refundAmount);
            PointTransactionResult cancelResult = PointTransactionResult.from(pointTransactionRepository.save(cancelTransaction));

            PointUseCancelResultBuilder pointUseCancelResultBuilder = PointUseCancelResult.builder()
                                                                                          .cancelPointTransaction(cancelResult);

            if (transaction.isExpiration()) {
                PointEarnResult pointEarnResult = pointService.earnPoints(
                    PointEarnCommand.of(transaction.getUserId(), refundAmount, defaultExpirationDays));
                pointUseCancelResultBuilder.reissuedPoint(pointEarnResult);
            }

            results.add(pointUseCancelResultBuilder.build());
        }

        return results;
    }

}
