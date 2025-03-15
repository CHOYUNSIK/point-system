package com.musinsa.point.controller.dto;


import com.musinsa.point.controller.validation.ValidPointAmount;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PointEarnRequest(
    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @ValidPointAmount
    Long amount,

    boolean isManual,

    @Min(value = 1, message = "만료일은 최소 1일 이상이어야 합니다.")
    @Max(value = 1825, message = "만료일은 최대 5년(1825일) 이하로 설정해야 합니다.")
    Integer expirationDays // nullable 허용
) {
}
