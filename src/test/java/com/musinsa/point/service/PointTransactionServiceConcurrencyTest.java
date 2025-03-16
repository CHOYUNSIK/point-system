package com.musinsa.point.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.musinsa.point.entity.Point;
import com.musinsa.point.entity.PointTransaction;
import com.musinsa.point.entity.enums.PointTransactionType;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointRepository;
import com.musinsa.point.repository.PointTransactionRepository;
import com.musinsa.point.service.dto.PointUseCancelCommand;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Slf4j
@SpringBootTest
class PointTransactionServiceConcurrencyTest {
    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private PointService pointService;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private PointRepository pointRepository;

    private static final long USER_ID = 1L;
    private static final long ORDER_ID = 1001L;
    private static final long INITIAL_USE_AMOUNT = 50_000L;
    private static final long CANCEL_AMOUNT_PER_THREAD = 10_000L;
    private static final int THREAD_COUNT = 10;

    @BeforeEach
    void setUp() {
        pointTransactionRepository.deleteAll();
        pointRepository.deleteAll();

        Point point = pointRepository.save(Point.builder()
                                                .userId(USER_ID)
                                                .amount(50_000L)
                                                .isManual(false)
                                                .expirationDate(LocalDateTime.now().plusDays(365))
                                                .build());

        pointTransactionRepository.save(PointTransaction.createUseTransaction(point, INITIAL_USE_AMOUNT, ORDER_ID));
    }

    @Test
    void shouldHandleConcurrentPointCancellation() throws InterruptedException {
        // Given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // When
        IntStream.range(0, THREAD_COUNT).forEach(i -> executorService.execute(() -> {
            try {
                PointUseCancelCommand command = new PointUseCancelCommand(ORDER_ID, CANCEL_AMOUNT_PER_THREAD);
                pointTransactionService.cancelUsedPoint(command);
            } catch (GeneralException e) {
                log.error("{} 번째 요청 실패: {}", i, e.getMessage(), e);
            } finally {
                latch.countDown();
            }
        }));

        latch.await();
        executorService.shutdown();

        // Then
        long totalCanceledAmount = pointTransactionRepository.findAll().stream()
                                                             .filter(pt -> pt.getTransactionType() == PointTransactionType.CANCEL)
                                                             .mapToLong(PointTransaction::getUsedAmount)
                                                             .sum();

        assertThat(totalCanceledAmount).isEqualTo(INITIAL_USE_AMOUNT);

        assertThatThrownBy(() -> pointTransactionService.cancelUsedPoint(new PointUseCancelCommand(ORDER_ID, 10_000L)))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("취소 가능한 금액을 초과하여 사용할 수 없습니다.");
    }
}
