package com.musinsa.point.repository;

import com.musinsa.point.entity.UserPointLimit;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface UserPointLimitRepository extends JpaRepository<UserPointLimit, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<UserPointLimit> findUserPointLimitsById(Long id);
}

