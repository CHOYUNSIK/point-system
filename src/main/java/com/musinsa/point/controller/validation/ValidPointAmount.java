package com.musinsa.point.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PointAmountValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPointAmount {
    String message() default "적립 가능 포인트 범위를 초과했습니다."; // 기본 메시지는 Validator에서 오버라이드됨
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
