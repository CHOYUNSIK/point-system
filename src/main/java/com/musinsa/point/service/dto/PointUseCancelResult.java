package com.musinsa.point.service.dto;


import lombok.Builder;

@Builder
public record PointUseCancelResult(
    PointTransactionResult cancelPointTransaction,
    PointEarnResult reissuedPoint
) {

}
