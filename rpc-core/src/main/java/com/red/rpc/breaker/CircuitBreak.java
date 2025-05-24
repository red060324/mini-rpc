package com.red.rpc.breaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
public class CircuitBreak {
    private State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);
    // 失败阈值
    private final int failureThreshold;
    // 成功阈值 (半开状态)
    private final double successRateInHalfOpen;
    // 熔断时间窗口
    private final long widowTime;

    private long lastFailTime = 0;

    public CircuitBreak(int failureThreshold, double successRateInHalfOpen, long widowTime) {
        this.failureThreshold = failureThreshold;
        this.successRateInHalfOpen = successRateInHalfOpen;
        this.widowTime = widowTime;
    }

    public synchronized boolean canReq() {
        switch (state) {
            case CLOSED:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastFailTime <= widowTime) {
                    return false;
                }
                state = State.HALF_OPEN;
                resetCounter();
                return true;
            case HALF_OPEN:
                totalCount.incrementAndGet();
                return true;
            default:
                throw new IllegalStateException("熔断器状态异常: " + state);
        }
    }

    public synchronized void success() {
        if (state != State.HALF_OPEN){
            resetCounter();
            return;
        }
        successCount.incrementAndGet();
        if (successCount.get() >= successRateInHalfOpen * totalCount.get()) {
            state = State.CLOSED;
            resetCounter();
        }
    }

    public synchronized void fail() {
        failureCount.incrementAndGet();
        lastFailTime = System.currentTimeMillis();

        if (state == State.HALF_OPEN){
            state = State.OPEN;
            return;
        }
        if (failureCount.get() >= failureThreshold) {
            state = State.OPEN;
        }
    }

    private void resetCounter() {
        failureCount.set(0);
        successCount.set(0);
        totalCount.set(0);
    }

    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

}
