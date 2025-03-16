package com.musinsa.point.controller.dto;


import com.musinsa.point.service.dto.PointEarnResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import com.musinsa.point.service.dto.PointUseCancelResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Schema(description = "포인트 사용 취소 응답")
@Builder(access = AccessLevel.PRIVATE)
public record PointUseCancelResponse(

    @Schema(description = "거래 ID", example = "100")
    long transactionId,
    @Schema(description = "취소된 포인트 금액", example = "5000")
    long usedAmount,
    @Schema(description = "주문 ID", example = "1001")
    long orderId,
    @Schema(description = "거래 유형", example = "CANCEL")
    String transactionType,
    @Schema(description = "원본 거래 ID (사용 취소된 원래 거래 ID)", example = "50", nullable = true)
    Long originalTransactionId,
    @Schema(description = "포인트 ID", example = "10")
    long pointId,
    @Schema(description = "사용자 ID", example = "1")
    long userId,
    @Schema(description = "포인트 금액", example = "10000")
    long amount,
    @Schema(description = "관리자 수기 지급 여부", example = "false")
    boolean isManual,
    @Schema(description = "포인트 만료일", example = "2024-12-31T23:59:59")
    LocalDateTime expirationDate,
    @Schema(description = "만료된 포인트로 인해 재적립된 포인트 정보", nullable = true)
    ReissuedPointDetail reissuedPoint

) {

    public static PointUseCancelResponse from(PointUseCancelResult cancelResult) {
        PointTransactionResult cancelPointTransaction = cancelResult.cancelPointTransaction();

        return PointUseCancelResponse.builder()
                                     .transactionId(cancelPointTransaction.id())
                                     .usedAmount(cancelPointTransaction.usedAmount())
                                     .orderId(cancelPointTransaction.orderId())
                                     .transactionType(cancelPointTransaction.transactionType())
                                     .originalTransactionId(cancelPointTransaction.originalTransactionId())
                                     .pointId(cancelPointTransaction.point().id())
                                     .userId(cancelPointTransaction.point().userId())
                                     .amount(cancelPointTransaction.point().amount())
                                     .isManual(cancelPointTransaction.point().isManual())
                                     .expirationDate(cancelPointTransaction.point().expirationDate())
                                     .reissuedPoint(cancelResult.reissuedPoint() != null
                                         ? ReissuedPointDetail.from(cancelResult.reissuedPoint()) : null)
                                     .build();


    }

    @Schema(description = "재적립된 포인트 정보 (만료된 포인트가 있을 경우)")
    @Builder(access = AccessLevel.PRIVATE)
    public record ReissuedPointDetail(
        @Schema(description = "재적립된 포인트 ID", example = "200")
        long id,
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "재적립된 포인트 금액", example = "5000")
        long amount,
        @Schema(description = "관리자 수기 지급 여부", example = "false")
        boolean isManual,
        @Schema(description = "재적립된 포인트의 만료일", example = "2025-01-01T00:00:00")
        LocalDateTime expirationDate

    ) {

        public static ReissuedPointDetail from(PointEarnResult point) {
            return ReissuedPointDetail.builder()
                                      .id(point.id())
                                      .userId(point.userId())
                                      .amount(point.amount())
                                      .isManual(point.isManual())
                                      .expirationDate(point.expirationDate())
                                      .build();
        }
    }

}
