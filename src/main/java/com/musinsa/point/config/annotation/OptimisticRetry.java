package com.musinsa.point.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
    retryFor = ObjectOptimisticLockingFailureException.class,
    maxAttempts = 5,
    backoff = @Backoff(delay = 100, multiplier = 2)
)
public @interface OptimisticRetry {

}
