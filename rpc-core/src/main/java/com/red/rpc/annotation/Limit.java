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
public @interface Limit {
    double permitsPerSecond(); // 每秒允许的请求数

    long timeout(); // 超时时间，单位为秒
}
