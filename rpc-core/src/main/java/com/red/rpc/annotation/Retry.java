package com.red.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author red
 * @date 2025/5/23
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    Class<? extends Throwable> value() default Exception.class;
    int maxAttempts() default 3;
    long delay() default 0;
}
