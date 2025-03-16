package com.musinsa.point.service.dto;

import com.musinsa.point.controller.dto.PointUseRequest;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PointUseCommand(
    long userId,
    long orderId,
    long useAmount
) {

    public static PointUseCommand from(PointUseRequest request) {
        return PointUseCommand.builder()
                              .userId(request.userId())
                              .orderId(request.orderId())
                              .useAmount(request.useAmount())
                              .build();
    }

}
