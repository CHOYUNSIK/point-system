package com.musinsa.point.service.dto;

import com.musinsa.point.entity.PointTransaction;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointTransactionResult(
    long id,
    long usedAmount,
    long orderId,
    String transactionType,
    Long originalTransactionId,
    PointResult point

) {

    public static PointTransactionResult from(PointTransaction entity) {
        return PointTransactionResult.builder()
                                     .id(entity.getId())
                                     .usedAmount(entity.getUsedAmount())
                                     .orderId(entity.getOrderId())
                                     .transactionType(entity.getTransactionType().name())
                                     .originalTransactionId(entity.getOriginalTransactionId())
                                     .point(PointResult.from(entity.getPoint()))
                                     .build();
    }

}
