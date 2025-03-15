package com.musinsa.point.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PointAmountValidator implements ConstraintValidator<ValidPointAmount, Long> {

    @Value("${point.min-earn-amount}")
    private long minEarnAmount;

    @Value("${point.max-earn-amount}")
    private long maxEarnAmount;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("amount는 필수 입력값입니다.").addConstraintViolation();
            return false;
        }

        boolean isValid = value >= minEarnAmount && value <= maxEarnAmount;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("적립 가능 포인트는 %d ~ %d 사이여야 합니다.", minEarnAmount, maxEarnAmount)
            ).addConstraintViolation();
        }

        return isValid;
    }
}
