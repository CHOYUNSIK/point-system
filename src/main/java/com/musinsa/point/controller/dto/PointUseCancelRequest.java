package com.musinsa.point.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "포인트 사용 취소 요청")
public record PointUseCancelRequest(
    @Schema(description = "주문 ID", example = "1001")
    @NotNull(message = "orderId는 필수입니다.")
    Long orderId,
    @Schema(description = "취소할 포인트 금액", example = "5000")
    @Min(value = 1, message = "취소할 금액은 1 이상이어야 합니다.")
    Long cancelAmount
) {
}
