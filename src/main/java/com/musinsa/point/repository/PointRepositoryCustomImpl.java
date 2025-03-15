package com.musinsa.point.repository;

import com.musinsa.point.entity.QPoint;
import com.musinsa.point.entity.QPointTransaction;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PointRepositoryCustomImpl implements PointRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public int deleteIfNotUsed(Long pointId) {

        QPoint qPoint = QPoint.point;
        QPointTransaction qTransaction = QPointTransaction.pointTransaction;

        return (int) queryFactory
            .delete(qPoint)
            .where(qPoint.id.eq(pointId)
                            .and(
                                JPAExpressions
                                    .select(qTransaction.usedAmount.sum().coalesce(0L))
                                    .from(qTransaction)
                                    .where(qTransaction.point.id.eq(qPoint.id))
                                    .eq(0L)
                            )
            )
            .execute();
    }
}
