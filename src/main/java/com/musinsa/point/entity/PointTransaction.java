package com.musinsa.point.entity;

import com.musinsa.point.entity.enums.PointTransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder(access = AccessLevel.PRIVATE)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(indexes = {
    @Index(name = "idx_order_id", columnList = "orderId"),
    @Index(name = "idx_point_id", columnList = "pointId")
})
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @Column(nullable = false)
    private long usedAmount;

    @Column(nullable = false)
    private long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType transactionType;

    private Long originalTransactionId;

    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public long getUserId(){
       return this.point.getUserId();
    }

    public boolean isUsed() {
        return PointTransactionType.USE == this.transactionType;
    }

    public boolean isCanceled() {
        return PointTransactionType.CANCEL == this.transactionType;
    }

    public boolean isExpiration() {
        return point.isExpiration();
    }


    public static PointTransaction createUseTransaction(Point point, long usedAmount, long orderId) {
        return PointTransaction.builder()
                               .point(point)
                               .usedAmount(usedAmount)
                               .orderId(orderId)
                               .transactionType(PointTransactionType.USE)
                               .build();
    }

    public static PointTransaction createUseCancelTransaction(
        PointTransaction originalTransaction,
        long usedAmount
    ) {
        return PointTransaction.builder()
                               .point(originalTransaction.point)
                               .usedAmount(usedAmount)
                               .orderId(originalTransaction.orderId)
                               .transactionType(PointTransactionType.CANCEL)
                               .originalTransactionId(originalTransaction.id)
                               .build();
    }

}
