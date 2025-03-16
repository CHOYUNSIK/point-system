package com.musinsa.point.service.dto;

import com.musinsa.point.entity.Point;
import com.musinsa.point.entity.PointTransaction;
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

    public static PointResult of(Point point, List<PointTransaction> pointTransactions) {
        List<PointTransactionResult> transactionResults = pointTransactions.stream()
                                                                           .map(PointTransactionResult::from)
                                                                           .toList();

        return create(point, transactionResults);
    }

    public static PointResult from(Point point) {
        return create(point, List.of()); // 기본적으로 빈 리스트 전달
    }

    private static PointResult create(Point point, List<PointTransactionResult> transactions) {
        return PointResult.builder()
                          .id(point.getId())
                          .userId(point.getUserId())
                          .amount(point.getAmount())
                          .isManual(point.isManual())
                          .expirationDate(point.getExpirationDate())
                          .usedAmount(point.getUsedAmount())
                          .availableAmount(point.getAvailableAmount())
                          .pointTransactionList(transactions)
                          .build();
    }
}
