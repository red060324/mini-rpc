package com.red.rpc.spi;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
