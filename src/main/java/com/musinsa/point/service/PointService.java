package com.musinsa.point.service;

import com.musinsa.point.entity.Point;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointRepository;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointEarnResult;
import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointUseCommand;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    @Value("${point.min-earn-amount}")
    private long minEarnAmount;

    @Value("${point.max-earn-amount}")
    private long maxEarnAmount;

    private final PointRepository pointRepository;

    private final UserPointLimitService userPointLimitService;

    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public PointEarnResult earnPoints(PointEarnCommand command) {
        validatePointAmount(command.amount());

        long totalAvailableBalance = getTotalAvailableBalance(command.userId());

        userPointLimitService.validateUserPointLimit(command.userId(), totalAvailableBalance);

        return PointEarnResult.from(pointRepository.save(command.toEntity()));

    }


    @Transactional
    public long getTotalAvailableBalance(long userId) {
        /*List<Point> validPoints = pointRepository.findByUserIdAndExpirationDateAfterOrderByIsManualDescExpirationDateAsc(
            userId,
            LocalDateTime.now()
        );*/

        List<Point> validPoints = pointRepository.findUsablePoints(userId);

        return validPoints.stream()
                          .mapToLong(Point::getAvailableAmount)
                          .sum();
    }

    private void validatePointAmount(long amount) {
        if (amount < minEarnAmount || amount > maxEarnAmount) {
            throw new GeneralException(
                ErrorCode.BAD_REQUEST,
                String.format("적립 가능 포인트는 %d ~ %d 사이여야 합니다.", minEarnAmount, maxEarnAmount)
            );
        }
    }

    @Transactional
    public void cancelPoint(Long pointId) {
        pointRepository.findById(pointId)
                       .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST, "해당 포인트 적립 내역을 찾을 수 없습니다."));

        int deletedRows = pointRepository.deleteIfNotUsed(pointId);

        if (deletedRows == 0) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "포인트가 이미 사용되어 취소할 수 없습니다.");
        }
    }


    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public List<PointResult> usePoints(PointUseCommand command) {
        long userId = command.userId();
        long totalAvailableBalance = getTotalAvailableBalance(userId);
        long useAmount  = command.useAmount();
        if (totalAvailableBalance < useAmount ) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "사용 가능한 포인트가 부족합니다.");
        }

        List<Point> pointList = pointRepository.findUsablePoints(userId);
        List<PointResult> pointResultList = new ArrayList<>();
        long remainingAmount = useAmount;
        for (Point point : pointList) {
            if (remainingAmount == 0) break;
            long useThisTime = Math.min(point.getAvailableAmount(), remainingAmount);
            point.usePoints(useThisTime, command.orderId());
            pointResultList.add(PointResult.from(pointRepository.save(point)));
            remainingAmount -= useThisTime;
        }
        if (remainingAmount > 0) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "포인트 사용 중 오류가 발생했습니다.");
        }
        return pointResultList;
    }
}

