package com.musinsa.point.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.musinsa.point.entity.PointTransaction;
import com.musinsa.point.entity.enums.PointTransactionType;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointTransactionRepository;
import com.musinsa.point.service.dto.PointUseCancelCommand;
import com.musinsa.point.service.dto.PointUseCancelResult;
import java.util.Collections;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PointTransactionServiceTest {
    @Mock
    private PointTransactionRepository pointTransactionRepository;
    @Mock
    private PointService pointService;
    @InjectMocks
    private PointTransactionService pointTransactionService;

    private static final long ORDER_ID = 1001L;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pointTransactionService, "defaultExpirationDays", 365);
    }
    @Test
    void shouldCancelUsedPointsSuccessfully() {
        // Given
        long cancelAmount = 5000;
        PointUseCancelCommand command = new PointUseCancelCommand(ORDER_ID, cancelAmount);

        List<PointTransaction> transactionList = Instancio.ofList(PointTransaction.class)
                                                          .size(1)
                                                          .set(Select.field(PointTransaction::getId), 1L)
                                                          .set(Select.field(PointTransaction::getUsedAmount), 10_000L) // 사용한 포인트
                                                          .set(Select.field(PointTransaction::getTransactionType), PointTransactionType.USE)
                                                          .create();

        given(pointTransactionRepository.findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(anyLong(), any()))
            .willReturn(transactionList);

        given(pointTransactionRepository.findByOriginalTransactionIdAndTransactionType(anyLong(), any()))
            .willReturn(Collections.emptyList()); // 아직 취소된 내역 없음

        given(pointTransactionRepository.save(any(PointTransaction.class))).willAnswer(invocation -> {
            PointTransaction pointTransaction = invocation.getArgument(0);

            return Instancio.of(PointTransaction.class)
                            .set(Select.field(PointTransaction::getId), 100L)
                            .set(Select.field(PointTransaction::getPoint), pointTransaction.getPoint())
                            .set(Select.field(PointTransaction::getUsedAmount), pointTransaction.getUsedAmount())
                            .set(Select.field(PointTransaction::getOrderId), pointTransaction.getOrderId())
                            .set(Select.field(PointTransaction::getTransactionType), pointTransaction.getTransactionType())
                            .set(Select.field(PointTransaction::getOriginalTransactionId), pointTransaction.getOriginalTransactionId())
                            .create();
        });
        // When
        List<PointUseCancelResult> results = pointTransactionService.cancelUsedPoint(command);

        // Then
        assertThat(results).hasSize(1);
        verify(pointTransactionRepository, times(1)).save(any(PointTransaction.class));
    }

    @Test
    void shouldThrowExceptionWhenCancelAmountIsZeroOrNegative() {
        // Given
        PointUseCancelCommand command = new PointUseCancelCommand(ORDER_ID, 0);

        // When & Then
        assertThatThrownBy(() -> pointTransactionService.cancelUsedPoint(command))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("취소 금액은 1원 이상이어야 합니다.");

        verify(pointTransactionRepository, never()).findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(anyLong(), any());
    }

    @Test
    void shouldThrowExceptionWhenNoTransactionFound() {
        // Given
        PointUseCancelCommand command = new PointUseCancelCommand(ORDER_ID, 5000);

        given(pointTransactionRepository.findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(anyLong(), any()))
            .willReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> pointTransactionService.cancelUsedPoint(command))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("해당 포인트 사용 내역을 찾을 수 없습니다.");

        verify(pointTransactionRepository, never()).save(any(PointTransaction.class));
    }

    @Test
    void shouldThrowExceptionWhenCancelAmountExceedsAvailablePoints() {
        // Given
        long cancelAmount = 20_000;
        PointUseCancelCommand command = new PointUseCancelCommand(ORDER_ID, cancelAmount);

        List<PointTransaction> transactionList = Instancio.ofList(PointTransaction.class)
                                                          .size(1)
                                                          .set(Select.field(PointTransaction::getId), 1L)
                                                          .set(Select.field(PointTransaction::getUsedAmount), 10_000L) // 사용한 포인트
                                                          .set(Select.field(PointTransaction::getTransactionType), PointTransactionType.USE)
                                                          .create();

        given(pointTransactionRepository.findByOrderIdAndTransactionTypeOrderByCreatedAtAsc(anyLong(), any()))
            .willReturn(transactionList);

        given(pointTransactionRepository.findByOriginalTransactionIdAndTransactionType(anyLong(), any()))
            .willReturn(Collections.emptyList()); // 취소된 내역 없음

        // When & Then
        assertThatThrownBy(() -> pointTransactionService.cancelUsedPoint(command))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("취소 가능한 금액을 초과하여 사용할 수 없습니다.");

        verify(pointTransactionRepository, never()).save(any(PointTransaction.class));
    }

}
