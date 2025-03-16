package com.musinsa.point.controller.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PointUseCancelRequest(
    @NotNull(message = "orderId는 필수입니다.")
    Long orderId,
    @Min(value = 1, message = "취소할 금액은 1 이상이어야 합니다.")
    Long cancelAmount
) {
}
