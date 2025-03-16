package com.musinsa.point.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Table(indexes = {
    @Index(name = "idx_point_user_id", columnList = "userId"),
    @Index(name = "idx_expiration_date", columnList = "expirationDate")
})
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private boolean isManual;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointTransaction> transactions = new ArrayList<>();


    public long getUsedAmount() {
        return transactions.stream()
                           .mapToLong(transaction -> {
                               if (transaction.isUsed()) {
                                   return transaction.getUsedAmount();
                               } else if (transaction.isCanceled()) {
                                   return -transaction.getUsedAmount();
                               }
                               return 0;
                           })
                           .sum();
    }


    public long getAvailableAmount() {
        return amount - getUsedAmount();
    }

    public void usePoints(long usedAmount, long orderId) {
        if (usedAmount > getAvailableAmount()) {
            throw new GeneralException(ErrorCode.BAD_REQUEST, "포인트 사용 가능 금액 초과");
        }
        PointTransaction pointTransaction = PointTransaction.createUseTransaction(this, usedAmount, orderId);
        this.transactions.add(pointTransaction);
    }
}




