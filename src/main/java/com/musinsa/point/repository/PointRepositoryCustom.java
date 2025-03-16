package com.musinsa.point.repository;

import com.musinsa.point.entity.Point;
import java.util.List;

public interface PointRepositoryCustom {
    int deleteIfNotUsed(Long pointId);

    List<Point> findUsablePoints(Long userId);
}
