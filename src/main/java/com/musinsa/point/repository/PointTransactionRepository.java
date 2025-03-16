package com.musinsa.point.repository;

import com.musinsa.point.entity.PointTransaction;
import com.musinsa.point.entity.enums.PointTransactionType;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;


public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long>{

    List<PointTransaction> findByOriginalTransactionIdAndTransactionType(long originalTransactionId, PointTransactionType transactionType);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    List<PointTransaction> findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(long orderId, PointTransactionType transactionType);
}

