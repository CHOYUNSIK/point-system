package com.musinsa.point.controller.dto;


import com.musinsa.point.service.dto.PointEarnResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import com.musinsa.point.service.dto.PointUseCancelResult;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointUseCancelResponse(
    long transactionId,
    long usedAmount,
    long orderId,
    String transactionType,
    Long originalTransactionId,
    long pointId,
    long userId,
    long amount,
    boolean isManual,
    LocalDateTime expirationDate,
    ReissuedPointDetail reissuedPoint

) {

    public static PointUseCancelResponse from(PointUseCancelResult cancelResult) {
        PointTransactionResult cancelPointTransaction = cancelResult.cancelPointTransaction();

        return PointUseCancelResponse.builder()
                                     .transactionId(cancelPointTransaction.id())
                                     .usedAmount(cancelPointTransaction.usedAmount())
                                     .orderId(cancelPointTransaction.orderId())
                                     .transactionType(cancelPointTransaction.transactionType())
                                     .originalTransactionId(cancelPointTransaction.originalTransactionId())
                                     .pointId(cancelPointTransaction.point().id())
                                     .userId(cancelPointTransaction.point().userId())
                                     .amount(cancelPointTransaction.point().amount())
                                     .isManual(cancelPointTransaction.point().isManual())
                                     .expirationDate(cancelPointTransaction.point().expirationDate())
                                     .reissuedPoint(cancelResult.reissuedPoint() != null
                                         ? ReissuedPointDetail.from(cancelResult.reissuedPoint()) : null)
                                     .build();


    }

    @Builder(access = AccessLevel.PRIVATE)
    public record ReissuedPointDetail(
        long id,
        long userId,
        long amount,
        boolean isManual,
        LocalDateTime expirationDate

    ) {

        public static ReissuedPointDetail from(PointEarnResult point) {
            return ReissuedPointDetail.builder()
                                      .id(point.id())
                                      .userId(point.userId())
                                      .amount(point.amount())
                                      .isManual(point.isManual())
                                      .expirationDate(point.expirationDate())
                                      .build();
        }
    }

}
