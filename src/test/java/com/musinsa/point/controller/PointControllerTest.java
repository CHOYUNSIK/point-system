package com.musinsa.point.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.controller.dto.PointEarnRequest;
import com.musinsa.point.controller.dto.PointUseCancelRequest;
import com.musinsa.point.controller.dto.PointUseRequest;
import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.exception.GeneralException;
import com.musinsa.point.service.PointService;
import com.musinsa.point.service.PointTransactionService;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointEarnResult;
import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointTransactionResult;
import com.musinsa.point.service.dto.PointUseCancelCommand;
import com.musinsa.point.service.dto.PointUseCancelResult;
import com.musinsa.point.service.dto.PointUseCommand;
import java.time.LocalDateTime;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PointService pointService;

    @MockBean
    private PointTransactionService pointTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private final MediaType contentType = MediaType.APPLICATION_JSON;

    @DisplayName("[API][POST] 포인트 적립 성공")
    @Test
    void earnPoints_Success() throws Exception {

        PointEarnRequest request = new PointEarnRequest(1L, 500L, false, 365);
        PointEarnResult result = new PointEarnResult(
            1L,
            request.userId(),
            request.amount(),
            request.isManual(),
            false,
            LocalDateTime.now().plusDays(request.expirationDays())
        );

        given(pointService.earnPoints(any(PointEarnCommand.class))).willReturn(result);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.userId").value(result.userId()))
           .andExpect(jsonPath("$.amount").value(result.amount()))
           .andExpect(jsonPath("$.isManual").value(result.isManual()));

        then(pointService).should(times(1)).earnPoints(any(PointEarnCommand.class));
    }

    @DisplayName("[API][POST] 포인트 적립 실패 - 요청 값 검증 오류")
    @ParameterizedTest
    @ValueSource(longs = {0L, -100L, 200000L})
        // long 타입 사용
    void earnPoints_ValidationError(long invalidAmount) throws Exception {
        // Given
        PointEarnRequest request = Instancio.of(PointEarnRequest.class)
                                            .set(Select.field(PointEarnRequest::amount), invalidAmount)
                                            .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointService).shouldHaveNoInteractions();
    }

    @DisplayName("[API][POST] 포인트 적립 실패 - userId가 null이면 실패")
    @ParameterizedTest
    @ValueSource(longs = {0L})
        // 의미 없는 값 (실제 테스트에서는 `null` 사용)
    void earnPoints_UserIdIsNull() throws Exception {
        // Given
        PointEarnRequest request = Instancio.of(PointEarnRequest.class)
                                            .set(Select.field(PointEarnRequest::userId), null)
                                            .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointService).shouldHaveNoInteractions();
    }

    @DisplayName("[API][POST] 포인트 적립 실패 - amount가 1~100,000 범위를 벗어나면 실패")
    @ParameterizedTest
    @ValueSource(longs = {0L, -100L, 100001L})
        // 1~100,000 범위를 벗어난 값
    void earnPoints_AmountOutOfRange(long invalidAmount) throws Exception {
        // Given
        PointEarnRequest request = Instancio.of(PointEarnRequest.class)
                                            .set(Select.field(PointEarnRequest::amount), invalidAmount)
                                            .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointService).shouldHaveNoInteractions();
    }

    @DisplayName("[API][POST] 포인트 적립 실패 - expirationDays가 1~1825 범위를 벗어나면 실패")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 1826})
        // 1~1825 범위를 벗어난 값
    void earnPoints_ExpirationDaysOutOfRange(int invalidExpirationDays) throws Exception {
        // Given
        PointEarnRequest request = Instancio.of(PointEarnRequest.class)
                                            .set(Select.field(PointEarnRequest::expirationDays), invalidExpirationDays)
                                            .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointService).shouldHaveNoInteractions();
    }

    @DisplayName("[API][DELETE] 포인트 적립 취소 성공")
    @Test
    void cancelPoint_Success() throws Exception {
        // Given
        willDoNothing().given(pointService).cancelPoint(1L);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.delete("/{id}", 1L)
                                          .contentType(contentType))
           .andExpect(status().isOk());

        then(pointService).should(times(1)).cancelPoint(1L);
    }

    @DisplayName("[API][DELETE] 포인트 적립 취소 실패 - 포인트가 존재하지 않음")
    @Test
    void cancelPoint_PointNotFound() throws Exception {
        // Given
        willThrow(new GeneralException(ErrorCode.BAD_REQUEST, "해당 포인트 적립 내역을 찾을 수 없습니다."))
            .given(pointService).cancelPoint(2L);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.delete("/{id}", 2L)
                                          .contentType(contentType))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("해당 포인트 적립 내역을 찾을 수 없습니다.")));

        then(pointService).should(times(1)).cancelPoint(2L);
    }

    @DisplayName("[API][DELETE] 포인트 적립 취소 실패 - 이미 사용된 포인트")
    @Test
    void cancelPoint_PointAlreadyUsed() throws Exception {
        // Given
        willThrow(new GeneralException(ErrorCode.BAD_REQUEST, "포인트가 이미 사용되어 취소할 수 없습니다."))
            .given(pointService).cancelPoint(3L);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.delete("/{id}", 3L)
                                          .contentType(contentType))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("포인트가 이미 사용되어 취소할 수 없습니다.")));

        then(pointService).should(times(1)).cancelPoint(3L);
    }


    @DisplayName("[API][POST] 포인트 사용 성공")
    @Test
    void usePoints_Success() throws Exception {
        // Given
        PointUseRequest request = new PointUseRequest(1L, 1001L, 5_000L);
        List<PointResult> pointResults = Instancio.ofList(PointResult.class)
                                                  .size(1)
                                                  .set(Select.field(PointResult::id), 1L)
                                                  .set(Select.field(PointResult::userId), request.userId())
                                                  .set(Select.field(PointResult::amount), 10_000L)
                                                  .set(Select.field(PointResult::usedAmount), request.useAmount())
                                                  .create();

        given(pointService.usePoints(any(PointUseCommand.class))).willReturn(pointResults);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.size()").value(1))
           .andExpect(jsonPath("$[0].userId").value(request.userId()))
           .andExpect(jsonPath("$[0].usedAmount").value(request.useAmount()));

        then(pointService).should(times(1)).usePoints(any(PointUseCommand.class));
    }

    @DisplayName("[API][POST] 포인트 사용 실패 - 사용 가능 포인트 부족")
    @Test
    void usePoints_InsufficientPoints() throws Exception {
        // Given
        PointUseRequest request = new PointUseRequest(1L, 1002L, 20_000L);

        willThrow(new GeneralException(ErrorCode.BAD_REQUEST, "사용 가능한 포인트가 부족합니다."))
            .given(pointService).usePoints(any(PointUseCommand.class));

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("사용 가능한 포인트가 부족합니다.")));

        then(pointService).should(times(1)).usePoints(any(PointUseCommand.class));
    }

    @DisplayName("[API][POST] 포인트 사용 실패 - 요청 값 검증 오류")
    @ParameterizedTest
    @ValueSource(longs = {0L, -100L}) // 잘못된 포인트 사용 금액
    void usePoints_ValidationError(long invalidUseAmount) throws Exception {
        // Given
        PointUseRequest request = Instancio.of(PointUseRequest.class)
                                           .set(Select.field(PointUseRequest::useAmount), invalidUseAmount)
                                           .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointService).shouldHaveNoInteractions();
    }

    @DisplayName("[API][POST] 포인트 사용 취소 성공")
    @Test
    void cancelUsedPoint_Success() throws Exception {
        // Given
        PointUseCancelRequest request = new PointUseCancelRequest(1001L, 5_000L);

        List<PointUseCancelResult> cancelResults = Instancio.ofList(PointUseCancelResult.class)
                                                            .size(1)
                                                            .set(Select.field(PointUseCancelResult::cancelPointTransaction), Instancio.create(
                                                                PointTransactionResult.class))
                                                            .set(Select.field(PointUseCancelResult::reissuedPoint), Instancio.create(PointEarnResult.class))
                                                            .create();

        given(pointTransactionService.cancelUsedPoint(any(PointUseCancelCommand.class)))
            .willReturn(cancelResults);

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use/cancel")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.size()").value(1));


        then(pointTransactionService).should(times(1)).cancelUsedPoint(any(PointUseCancelCommand.class));
    }

    @DisplayName("[API][POST] 포인트 사용 취소 실패 - 취소 가능 금액 초과")
    @Test
    void cancelUsedPoint_ExceedsCancelableAmount() throws Exception {
        // Given
        PointUseCancelRequest request = new PointUseCancelRequest(1001L, 20_000L);

        willThrow(new GeneralException(ErrorCode.BAD_REQUEST, "취소 가능한 금액을 초과하여 사용할 수 없습니다."))
            .given(pointTransactionService).cancelUsedPoint(any(PointUseCancelCommand.class));

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use/cancel")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("취소 가능한 금액을 초과하여 사용할 수 없습니다.")));

        then(pointTransactionService).should(times(1)).cancelUsedPoint(any(PointUseCancelCommand.class));
    }

    @DisplayName("[API][POST] 포인트 사용 취소 실패 - 요청 값 검증 오류")
    @ParameterizedTest
    @ValueSource(longs = {0L, -100L})  // 잘못된 취소 금액
    void cancelUsedPoint_ValidationError(long invalidCancelAmount) throws Exception {
        // Given
        PointUseCancelRequest request = Instancio.of(PointUseCancelRequest.class)
                                                 .set(Select.field(PointUseCancelRequest::cancelAmount), invalidCancelAmount)
                                                 .create();

        // When & Then
        mvc.perform(MockMvcRequestBuilders.post("/use/cancel")
                                          .contentType(contentType)
                                          .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("VALIDATION ERRORS")));

        then(pointTransactionService).shouldHaveNoInteractions();
    }


}
