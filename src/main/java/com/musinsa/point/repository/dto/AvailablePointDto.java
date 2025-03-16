package com.musinsa.point.repository.dto;

import java.time.LocalDateTime;

public record AvailablePointDto(
    Long id,
    Long userId,
    Long amount,
    boolean isManual,
    LocalDateTime expirationDate,
    Long availableAmount
) {

}
