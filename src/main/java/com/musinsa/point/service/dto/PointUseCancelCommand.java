package com.musinsa.point.service.dto;


import com.musinsa.point.controller.dto.PointUseCancelRequest;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointUseCancelCommand(
    long orderId,
    long cancelAmount
) {

    public static PointUseCancelCommand from(PointUseCancelRequest request) {
        return PointUseCancelCommand.builder()
               .orderId(request.orderId())
               .cancelAmount(request.cancelAmount())
               .build();
    }
}
