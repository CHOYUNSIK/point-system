package com.musinsa.point.controller.dto;

import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointUseResponse(
    long id,
    long userId,
    long amount,
    boolean isManual,
    LocalDateTime expirationDate,
    long usedAmount,
    long availableAmount,
    List<PointTransactionDetail> pointTransaction
) {

    @Builder(access = AccessLevel.PRIVATE)
    public record PointTransactionDetail(
        long id,
        long usedAmount,
        long orderId,
        String transactionType,
        Long originalTransactionId

    ) {
        public static PointTransactionDetail from(PointTransactionResult transaction) {
            return PointTransactionDetail.builder()
                                         .id(transaction.id())
                                         .usedAmount(transaction.usedAmount())
                                         .orderId(transaction.orderId())
                                         .transactionType(transaction.transactionType())
                                         .originalTransactionId(transaction.originalTransactionId())
                                         .build();
        }
    }

    public static PointUseResponse from(PointResult point) {
        return PointUseResponse.builder()
                          .id(point.id())
                          .userId(point.id())
                          .amount(point.amount())
                          .isManual(point.isManual())
                          .expirationDate(point.expirationDate())
                          .usedAmount(point.usedAmount())
                          .availableAmount(point.availableAmount())
                          .pointTransaction(point.pointTransactionList().stream().map(PointTransactionDetail::from).toList())
                          .build();
    }
}
