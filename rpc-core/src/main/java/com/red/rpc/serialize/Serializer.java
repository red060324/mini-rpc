package com.red.rpc.serialize;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 待序列化对象
     * @return byte[]
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 待反序列化字节数组
     * @param clazz 反序列化对象类型
     * @param <T>   泛型
     * @return T
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
