package com.musinsa.point.service;

import com.musinsa.point.entity.Point;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointRepository;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointEarnResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    @Value("${point.min-earn-amount}")
    private long minEarnAmount;

    @Value("${point.max-earn-amount}")
    private long maxEarnAmount;

    private final PointRepository pointRepository;

    private final UserPointLimitService userPointLimitService;

    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public PointEarnResult earnPoints(PointEarnCommand command) {
        validatePointAmount(command.amount());

        long totalAvailableBalance = getTotalAvailableBalance(command.userId());

        userPointLimitService.validateUserPointLimit(command.userId(), totalAvailableBalance);

        return PointEarnResult.from(pointRepository.save(command.toEntity()));

    }


    @Transactional
    public long getTotalAvailableBalance(long userId) {
        List<Point> validPoints = pointRepository.findByUserIdAndExpirationDateAfter(userId, LocalDateTime.now());
        return validPoints.stream()
                          .mapToLong(Point::getAvailableAmount)
                          .sum();
    }

    private void validatePointAmount(long amount) {
        if (amount < minEarnAmount || amount > maxEarnAmount) {
            throw new GeneralException(
                ErrorCode.BAD_REQUEST,
                String.format("적립 가능 포인트는 %d ~ %d 사이여야 합니다.", minEarnAmount, maxEarnAmount)
            );
        }
    }

}

