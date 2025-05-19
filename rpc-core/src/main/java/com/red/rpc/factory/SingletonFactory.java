package com.red.rpc.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SingletonFactory {
    // 定义一个线程安全的缓存 Map，用于存储类与其对应实例的映射
    private static final Map<Class<?>, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

    /**
     * 获取指定类的单例实例。
     * 如果缓存中已存在该类的实例，则直接返回；
     * 否则通过反射创建实例并存入缓存后返回。
     *
     * @param clazz 需要获取单例的类的 Class 对象
     * @param <T>   泛型参数，表示需要获取的实例类型
     * @return 指定类的单例实例
     * @throws IllegalArgumentException 如果传入的 clazz 为 null
     */
    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz) {
        // 检查传入的 clazz 是否为 null，如果为 null 则抛出非法参数异常
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz为空");
        }

        // 检查缓存中是否已经存在该类的实例
        if (INSTANCE_CACHE.containsKey(clazz)) {
            // 如果存在，直接从缓存中取出并转换为对应的类型后返回
            return clazz.cast(INSTANCE_CACHE.get(clazz));
        }

        // 双重检查锁机制，确保多线程环境下的安全性
        synchronized (SingletonFactory.class) {
            // 再次检查缓存中是否存在该类的实例（避免并发问题）
            if (INSTANCE_CACHE.containsKey(clazz)) {
                return clazz.cast(INSTANCE_CACHE.get(clazz));
            }

            // 使用反射创建类的实例
            T t = clazz.getConstructor().newInstance();
            // 将创建的实例存入缓存
            INSTANCE_CACHE.put(clazz, t);
            // 返回创建的实例
            return t;
        }
    }
}