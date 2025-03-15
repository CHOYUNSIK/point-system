package com.musinsa.point.controller.dto;

import com.musinsa.point.service.dto.PointEarnResult;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointEarnResponse(
    long userId,
    long amount,
    boolean isManual,
    boolean isUsed,
    LocalDateTime expirationDate
) {

    public static PointEarnResponse from(PointEarnResult result) {
        return PointEarnResponse.builder()
                                .userId(result.userId())
                                .amount(result.amount())
                                .isManual(result.isManual())
                                .isUsed(result.isUsed())
                                .expirationDate(result.expirationDate())
                                .build();
    }
}
