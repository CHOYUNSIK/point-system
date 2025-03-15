package com.musinsa.point.service;

import com.musinsa.point.entity.UserPointLimit;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.UserPointLimitRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPointLimitService {

    private final UserPointLimitRepository userPointLimitRepository;

    @Transactional
    public void validateUserPointLimit(Long userId, long totalAvailablePoints) {
        Optional<UserPointLimit> optionalUserPointLimit = userPointLimitRepository.findUserPointLimitsById(userId);
        if (optionalUserPointLimit.isPresent()) {
            UserPointLimit userPointLimit = optionalUserPointLimit.get();
            long maxLimit = userPointLimit.getMaxPointLimit();
            if (totalAvailablePoints >= maxLimit) {
                long remainingPoints = maxLimit  - totalAvailablePoints;
                throw new GeneralException(
                    ErrorCode.BAD_REQUEST,
                    String.format("최대 보유 가능 포인트(%d)를 초과할 수 없습니다. 추가 가능 포인트: %d", maxLimit, remainingPoints)
                );
            }
        }
    }

}
