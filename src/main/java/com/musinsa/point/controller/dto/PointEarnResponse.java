package com.musinsa.point.controller.dto;

import com.musinsa.point.service.dto.PointEarnResult;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointEarnResponse(
    long id,
    long userId,
    long amount,
    boolean isManual,
    LocalDateTime expirationDate
) {

    public static PointEarnResponse from(PointEarnResult result) {
        return PointEarnResponse.builder()
                                .id(result.id())
                                .userId(result.userId())
                                .amount(result.amount())
                                .isManual(result.isManual())
                                .expirationDate(result.expirationDate())
                                .build();
    }
}
