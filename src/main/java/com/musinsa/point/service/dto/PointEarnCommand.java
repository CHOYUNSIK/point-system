package com.musinsa.point.service.dto;

import com.musinsa.point.controller.dto.PointEarnRequest;
import com.musinsa.point.entity.Point;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointEarnCommand(
    long userId,
    long amount,
    boolean isManual,
    int expirationDays
) {

    public static PointEarnCommand of(PointEarnRequest request, int defaultExpirationDays) {
        return PointEarnCommand.builder()
                               .userId(request.userId())
                               .amount(request.amount())
                               .isManual(request.isManual())
                               .expirationDays(
                                   request.expirationDays() != null ? request.expirationDays() : defaultExpirationDays
                               )
                               .build();
    }

    public Point toEntity() {
        return Point.builder()
                    .userId(userId)
                    .amount(amount)
                    .isManual(isManual)
                    .expirationDate(LocalDateTime.now().plusDays(expirationDays))
                    .build();
    }

    public long calculateTotalAvailablePoints(long userAvailablePoints) {
        return this.amount + userAvailablePoints;
    }
}
