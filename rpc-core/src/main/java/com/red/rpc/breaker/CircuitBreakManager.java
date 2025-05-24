package com.red.rpc.breaker;

import com.red.rpc.annotation.Breaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
public class CircuitBreakManager {
    private static final Map<String, CircuitBreak> BREAK_MAP = new ConcurrentHashMap<>();

    public static CircuitBreak get(String key, Breaker breaker) {
        return BREAK_MAP.computeIfAbsent(key, k -> new CircuitBreak(
                breaker.failureThreshold(),
                breaker.successRateInHalfOpen(),
                breaker.widowTime()));
    }

}
