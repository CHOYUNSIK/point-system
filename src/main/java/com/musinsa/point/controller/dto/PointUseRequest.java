package com.musinsa.point.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Schema(description = "포인트 사용 요청")
public record PointUseRequest(
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @Schema(description = "주문 ID", example = "1001")
    @NotNull(message = "orderId는 필수입니다.")
    Long orderId,

    @Schema(description = "사용할 포인트 금액", example = "5000")
    @Min(value = 1, message = "사용할 포인트는 1 이상이어야 합니다.")
    Long useAmount
) {
}
