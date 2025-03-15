package com.musinsa.point.service.dto;

import com.musinsa.point.entity.Point;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointEarnResult(

    long userId,
    long amount,
    boolean isManual,
    boolean isUsed,
    LocalDateTime expirationDate
) {

    public static PointEarnResult from(Point point) {
        return PointEarnResult.builder()
                              .userId(point.getUserId())
                              .amount(point.getAmount())
                              .isManual(point.isManual())
                              .expirationDate(point.getExpirationDate())
                              .build();
    }
}
