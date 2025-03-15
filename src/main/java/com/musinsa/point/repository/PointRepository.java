package com.musinsa.point.repository;

import com.musinsa.point.entity.Point;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;


public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {

    @Lock(LockModeType.OPTIMISTIC)
    List<Point> findByUserIdAndExpirationDateAfter(long userId, LocalDateTime now);




}

