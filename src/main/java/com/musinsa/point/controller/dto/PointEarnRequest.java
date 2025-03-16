package com.musinsa.point.controller.dto;


import com.musinsa.point.controller.validation.ValidPointAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "포인트 적립 요청")
public record PointEarnRequest(


    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @Schema(description = "적립할 포인트 금액", example = "5000")
    @ValidPointAmount
    Long amount,

    @Schema(description = "관리자 수기 지급 여부", example = "false")
    boolean isManual,

    @Schema(description = "포인트 만료일 (일 단위)", example = "365")
    @Min(value = 1, message = "만료일은 최소 1일 이상이어야 합니다.")
    @Max(value = 1825, message = "만료일은 최대 5년(1825일) 이하로 설정해야 합니다.")
    Integer expirationDays
) {
}
