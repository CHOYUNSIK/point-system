package com.musinsa.point.service;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.musinsa.point.entity.Point;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.repository.PointRepository;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointEarnResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserPointLimitService userPointLimitService;

    @InjectMocks
    private PointService pointService;

    private static final long USER_ID = 1L;
    private static final long TEST_POINT_ID = 1L; // 테스트용 ID

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pointService, "minEarnAmount", 1);
        ReflectionTestUtils.setField(pointService, "maxEarnAmount", 100_000);
    }
    @Test
    void shouldEarnPointsSuccessfully() {
        // Given
        PointEarnCommand command = new PointEarnCommand(USER_ID, 10_000, false,365);

        given(pointRepository.save(any(Point.class))).willAnswer(invocation -> {
            Point point = invocation.getArgument(0);
            return Point.builder()
                        .id(TEST_POINT_ID) // ✅ ID 값 설정
                        .userId(point.getUserId())
                        .amount(point.getAmount())
                        .expirationDate(point.getExpirationDate())
                        .isManual(point.isManual())
                        .build();
        });

        // When
        PointEarnResult result = pointService.earnPoints(command);

        // Then
        assertThat(result.amount()).isEqualTo(10_000);
        assertThat(result.userId()).isEqualTo(USER_ID);
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @Test
    void shouldThrowExceptionWhenAmountExceedsMaximum() {
        // Given
        PointEarnCommand command = new PointEarnCommand(USER_ID, 200_000, false,365);

        // When & Then
        assertThatThrownBy(() -> pointService.earnPoints(command))
                  .isInstanceOf(GeneralException.class)
                  .hasMessageContaining("적립 가능 포인트는");

        verify(pointRepository, never()).save(any(Point.class)); // 저장이 일어나면 안됨
    }

    @Test
    void shouldThrowExceptionWhenAmountIsBelowMinimum() {
        // Given
        PointEarnCommand command = new PointEarnCommand(USER_ID, 0, false,365);

        // When & Then
        assertThatThrownBy(() -> pointService.earnPoints(command))
                  .isInstanceOf(GeneralException.class)
                  .hasMessageContaining("적립 가능 포인트는");

        verify(pointRepository, never()).save(any(Point.class)); // 저장이 일어나면 안됨
    }

    @Test
    void shouldThrowExceptionWhenUserExceedsPointLimit() {
        // Given
        PointEarnCommand command = new PointEarnCommand(USER_ID, 50_000, false, 365);
        given(pointRepository.findByUserIdAndExpirationDateAfter(anyLong(), any()))
            .willReturn(List.of(Point.builder()
                                     .id(TEST_POINT_ID)
                                     .userId(USER_ID)
                                     .amount(100_000) // 이미 최대 적립됨
                                     .expirationDate(LocalDateTime.now().plusDays(365))
                                     .isManual(false)
                                     .build()));

        doThrow(new GeneralException(ErrorCode.BAD_REQUEST, "최대 보유 가능 포인트"))
            .when(userPointLimitService).validateUserPointLimit(anyLong(), anyLong());

        // When & Then
        assertThatThrownBy(() -> pointService.earnPoints(command))
                  .isInstanceOf(GeneralException.class)
                  .hasMessageContaining("최대 보유 가능 포인트");

        verify(pointRepository, never()).save(any(Point.class)); // 저장이 일어나면 안됨
    }

    @Test
    void shouldThrowExceptionWhenPointNotFound() {
        // Given
        given(pointRepository.findById(any())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pointService.cancelPoint(any()))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("해당 포인트 적립 내역을 찾을 수 없습니다.");

        // Verify
        then(pointRepository).should(times(1)).findById(any());
        then(pointRepository).should(never()).deleteIfNotUsed(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenPointIsAlreadyUsed() {
        // Given
        Point point = Instancio.create(Point.class);
        given(pointRepository.findById(any())).willReturn(Optional.of(point));
        given(pointRepository.deleteIfNotUsed(any())).willReturn(0); // 포인트 사용됨

        // When & Then
        assertThatThrownBy(() -> pointService.cancelPoint(any()))
            .isInstanceOf(GeneralException.class)
            .hasMessageContaining("포인트가 이미 사용되어 취소할 수 없습니다.");

        // Verify
        then(pointRepository).should(times(1)).findById(any());
        then(pointRepository).should(times(1)).deleteIfNotUsed(any());
    }

    @Test
    void shouldCancelPointSuccessfullyWhenNotUsed() {
        // Given
        Point point = Instancio.create(Point.class);
        given(pointRepository.findById(any())).willReturn(Optional.of(point));
        given(pointRepository.deleteIfNotUsed(any())).willReturn(1); // 정상 삭제

        // When & Then
        assertThatCode(() -> pointService.cancelPoint(any()))
            .doesNotThrowAnyException();

        // Verify
        then(pointRepository).should(times(1)).findById(any());
        then(pointRepository).should(times(1)).deleteIfNotUsed(any());
    }


}
