package com.musinsa.point.repository;

import com.musinsa.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {

}

