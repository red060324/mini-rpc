package com.red.rpc.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例工厂类
 * 用于为指定类提供线程安全的单例实例获取
 * 支持延迟加载和并发环境下的安全实例创建
 */
@Slf4j
public class SingletonFactory {
    // 用于缓存各类的单例实例，保证线程安全
    private static final Map<Class<?>, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    // 私有构造方法，防止外部实例化
    private SingletonFactory() {
    }

    /**
     * 获取指定类的单例对象。
     * 若缓存中存在则直接返回，否则通过反射创建并缓存。
     * 采用双重检查锁保证并发安全。
     *
     * @param clazz 目标类的 Class 对象
     * @param <T>   返回实例的类型
     * @return clazz 对应的单例对象
     * @throws IllegalArgumentException clazz 为空时抛出
     */
    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz) {
        // 校验参数
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz为空");
        }

        // 先检查缓存
        if (INSTANCE_CACHE.containsKey(clazz)) {
            return clazz.cast(INSTANCE_CACHE.get(clazz));
        }

        // 双重检查锁，保证多线程下只创建一个实例
        synchronized (SingletonFactory.class) {
            if (INSTANCE_CACHE.containsKey(clazz)) {
                return clazz.cast(INSTANCE_CACHE.get(clazz));
            }
            // 通过反射创建实例并缓存
            T t = clazz.getConstructor().newInstance();
            INSTANCE_CACHE.put(clazz, t);
            return t;
        }
    }
}
