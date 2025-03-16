package com.musinsa.point.controller.dto;

import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Schema(description = "포인트 사용 응답")
@Builder(access = AccessLevel.PRIVATE)
public record PointUseResponse(
    @Schema(description = "포인트 ID", example = "1")
    long id,
    @Schema(description = "사용자 ID", example = "1")
    long userId,
    @Schema(description = "포인트 금액", example = "10000")
    long amount,
    @Schema(description = "관리자 수기 지급 여부", example = "false")
    boolean isManual,
    @Schema(description = "포인트 만료일", example = "2024-12-31T23:59:59")
    LocalDateTime expirationDate,
    @Schema(description = "사용된 포인트 금액", example = "5000")
    long usedAmount,
    @Schema(description = "남은 포인트 금액", example = "5000")
    long availableAmount,
    @Schema(description = "포인트 거래 내역")
    List<PointTransactionDetail> pointTransaction
) {

    @Schema(description = "포인트 거래 상세 정보")
    @Builder(access = AccessLevel.PRIVATE)
    public record PointTransactionDetail(
        @Schema(description = "거래 ID", example = "100")
        long id,
        @Schema(description = "거래에서 사용된 포인트 금액", example = "5000")
        long usedAmount,
        @Schema(description = "거래가 속한 주문 ID", example = "1001")
        long orderId,
        @Schema(description = "거래 유형", example = "USE")
        String transactionType,
        @Schema(description = "원본 거래 ID (사용 취소 시 원본 거래를 참조)", example = "null", nullable = true)
        Long originalTransactionId

    ) {
        public static PointTransactionDetail from(PointTransactionResult transaction) {
            return PointTransactionDetail.builder()
                                         .id(transaction.id())
                                         .usedAmount(transaction.usedAmount())
                                         .orderId(transaction.orderId())
                                         .transactionType(transaction.transactionType())
                                         .originalTransactionId(transaction.originalTransactionId())
                                         .build();
        }
    }

    public static PointUseResponse from(PointResult point) {
        return PointUseResponse.builder()
                          .id(point.id())
                          .userId(point.id())
                          .amount(point.amount())
                          .isManual(point.isManual())
                          .expirationDate(point.expirationDate())
                          .usedAmount(point.usedAmount())
                          .availableAmount(point.availableAmount())
                          .pointTransaction(point.pointTransactionList().stream().map(PointTransactionDetail::from).toList())
                          .build();
    }
}
