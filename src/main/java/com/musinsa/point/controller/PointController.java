package com.musinsa.point.controller;


import com.musinsa.point.controller.dto.PointEarnRequest;
import com.musinsa.point.controller.dto.PointEarnResponse;
import com.musinsa.point.controller.dto.PointUseRequest;
import com.musinsa.point.controller.dto.PointUseResponse;
import com.musinsa.point.service.PointService;
import com.musinsa.point.service.dto.PointEarnCommand;
import com.musinsa.point.service.dto.PointResult;
import com.musinsa.point.service.dto.PointUseCommand;
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

    @PostMapping
    public ResponseEntity<PointEarnResponse> earnPoints(@Valid @RequestBody PointEarnRequest request) {
        PointEarnCommand command = PointEarnCommand.of(request, defaultExpirationDays);
        return ResponseEntity.ok(
            PointEarnResponse.from(pointService.earnPoints(command))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPoint(@PathVariable Long id) {
        pointService.cancelPoint(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/use")
    public ResponseEntity<List<PointUseResponse>> usePoints(@Valid @RequestBody PointUseRequest request) {
        List<PointResult> pointResults = pointService.usePoints(PointUseCommand.from(request));
        return ResponseEntity.ok(pointResults.stream().map(PointUseResponse::from).toList());
    }

}
