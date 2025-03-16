package com.musinsa.point.controller.dto;

import com.musinsa.point.service.dto.PointEarnResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Schema(description = "포인트 적립 응답")
@Builder(access = AccessLevel.PRIVATE)
public record PointEarnResponse(
    @Schema(description = "포인트 ID", example = "1")
    long id,
    @Schema(description = "사용자 ID", example = "1")
    long userId,
    @Schema(description = "적립된 포인트 금액", example = "5000")
    long amount,
    @Schema(description = "관리자 수기 지급 여부", example = "false")
    boolean isManual,
    @Schema(description = "포인트 만료일", example = "2024-12-31T23:59:59")
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
