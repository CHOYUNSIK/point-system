package com.musinsa.point.service.dto;

import com.musinsa.point.entity.Point;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointResult(
    long id,
    long userId,
    long amount,
    boolean isManual,
    LocalDateTime expirationDate,
    long usedAmount,
    long availableAmount,
    List<PointTransactionResult> pointTransactionList
) {

    public static PointResult from(Point point) {
        return PointResult.builder()
                          .id(point.getId())
                          .userId(point.getUserId())
                          .amount(point.getAmount())
                          .isManual(point.isManual())
                          .expirationDate(point.getExpirationDate())
                          .usedAmount(point.getUsedAmount())
                          .availableAmount(point.getAvailableAmount())
                          .pointTransactionList(point.getTransactions().stream().map(PointTransactionResult::from).toList())
                          .build();
    }
}
