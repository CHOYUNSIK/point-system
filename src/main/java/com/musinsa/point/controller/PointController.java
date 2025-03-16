package com.musinsa.point.controller;


import com.musinsa.point.controller.dto.PointEarnRequest;
import com.musinsa.point.controller.dto.PointEarnResponse;
import com.musinsa.point.controller.dto.PointUseCancelRequest;
import com.musinsa.point.controller.dto.PointUseCancelResponse;
import com.musinsa.point.controller.dto.PointUseRequest;
import com.musinsa.point.controller.dto.PointUseResponse;
import com.musinsa.point.service.PointService;
import com.musinsa.point.service.PointTransactionService;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointUseCancelCommand;
import com.musinsa.point.service.dto.PointUseCancelResult;
import com.musinsa.point.service.dto.PointUseCommand;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    @Value("${point.default-expiration-days}")
    private int defaultExpirationDays;

    private final PointService pointService;
    private final PointTransactionService pointTransactionService;

    @Operation(summary = "포인트 적립", description = "사용자에게 포인트를 적립합니다.")
    @PostMapping
    public ResponseEntity<PointEarnResponse> earnPoints(@Valid @RequestBody PointEarnRequest request) {
        PointEarnCommand command = PointEarnCommand.of(request, defaultExpirationDays);
        return ResponseEntity.ok(
            PointEarnResponse.from(pointService.earnPoints(command))
        );
    }

    @Operation(summary = "포인트 취소", description = "사용자의 포인트 적립을 취소합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPoint(@PathVariable Long id) {
        pointService.cancelPoint(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포인트 사용", description = "사용자가 보유한 포인트를 사용합니다.")
    @PostMapping("/use")
    public ResponseEntity<List<PointUseResponse>> usePoints(@Valid @RequestBody PointUseRequest request) {
        List<PointResult> pointResults = pointService.usePoints(PointUseCommand.from(request));
        return ResponseEntity.ok(pointResults.stream().map(PointUseResponse::from).toList());
    }

    @Operation(summary = "포인트 사용 취소", description = "사용했던 포인트를 취소합니다.")
    @PostMapping("/use/cancel")
    public ResponseEntity<List<PointUseCancelResponse>> cancelUsedPoint(@Valid @RequestBody PointUseCancelRequest request) {
        List<PointUseCancelResult> pointUseCancelResults = pointTransactionService.cancelUsedPoint(
            PointUseCancelCommand.from(request)
        );
        return ResponseEntity.ok(pointUseCancelResults.stream().map(PointUseCancelResponse::from).toList());
    }

}
