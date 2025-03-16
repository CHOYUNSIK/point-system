package com.musinsa.point.controller.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PointUseRequest(
    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @NotNull(message = "orderId는 필수입니다.")
    Long orderId,

    @Min(value = 1, message = "사용할 포인트는 1 이상이어야 합니다.")
    Long useAmount
) {
}
