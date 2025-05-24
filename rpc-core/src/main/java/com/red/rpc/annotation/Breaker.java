package com.red.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Breaker {
    int failureThreshold() default 20;
    double successRateInHalfOpen() default 0.5;
    long widowTime() default 10000L;
}
