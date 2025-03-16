package com.musinsa.point.repository;

import com.musinsa.point.entity.Point;
import com.musinsa.point.entity.QPoint;
import com.musinsa.point.entity.QPointTransaction;
import com.musinsa.point.entity.enums.PointTransactionType;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<Point> findUsablePoints(Long userId) {
        QPoint point = QPoint.point;
        QPointTransaction transaction = QPointTransaction.pointTransaction;

        NumberExpression<Long> usedAmount = new CaseBuilder()
            .when(transaction.transactionType.eq(PointTransactionType.USE)).then(transaction.usedAmount)
            .otherwise(0L)
            .sum();

        NumberExpression<Long> canceledAmount = new CaseBuilder()
            .when(transaction.transactionType.eq(PointTransactionType.CANCEL)).then(transaction.usedAmount)
            .otherwise(0L)
            .sum();

        NumberExpression<Long> availableAmount = point.amount.subtract(usedAmount).add(canceledAmount);

        return queryFactory
            .select(point)
            .from(point)
            .leftJoin(point.transactions, transaction)
            .where(
                point.userId.eq(userId),
                point.expirationDate.after(LocalDateTime.now())
            )
            .groupBy(point.id)
            .having(availableAmount.gt(0))
            .orderBy(point.isManual.desc(), point.expirationDate.asc())
            .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
            .fetch();
    }
}
