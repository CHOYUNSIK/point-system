package com.musinsa.point.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.musinsa.point.entity.UserPointLimit;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointRepository;
import com.musinsa.point.repository.UserPointLimitRepository;
import com.musinsa.point.service.dto.PointEarnCommand;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Slf4j
@SpringBootTest
class PointServiceConcurrencyTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserPointLimitRepository userPointLimitRepository;
    private static final long USER_ID = 1L;
    private static final int THREAD_COUNT = 10;
    @BeforeEach
    void setUp() {
        pointRepository.deleteAll();
    }

    @Test
    void shouldDetectRaceConditionWhenPointLimitExceeded() throws InterruptedException {
        // Given
        userPointLimitRepository.save(UserPointLimit.create(USER_ID, 50_000));
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        PointEarnCommand command = new PointEarnCommand(USER_ID, 10_000, false, 365);

        IntStream.range(0, THREAD_COUNT)
                 .forEach(i -> executorService.execute(() -> {
                     try {
                         pointService.earnPoints(command);
                         log.info("{} 번째 요청 성공", i);
                     } catch (Exception e) {
                         log.error("{} 번째 요청 실패: {}", i, e.getMessage(), e);
                     } finally {
                         latch.countDown();
                     }
                 }));

        latch.await(); // 모든 스레드가 종료될 때까지 대기
        executorService.shutdown();

        // Then
        Assertions.assertThat(pointService.getTotalAvailableBalance(command.userId())).isEqualTo(5_0000);
        assertThatThrownBy(() -> pointService.earnPoints(command))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("최대 보유 가능 포인트");
    }



}
