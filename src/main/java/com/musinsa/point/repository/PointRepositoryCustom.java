package com.musinsa.point.repository;

public interface PointRepositoryCustom {
    int deleteIfNotUsed(Long pointId);
}
