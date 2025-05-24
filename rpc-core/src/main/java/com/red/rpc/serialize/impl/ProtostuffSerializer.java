package com.red.rpc.serialize.impl;

import com.red.rpc.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 基于Protostuff的序列化实现，高性能、跨语言的二进制序列化方案。
 * @author red
 * @date 2025/5/24
 */
public class ProtostuffSerializer implements Serializer {
    // 线程安全的缓冲区，用于序列化
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    /**
     * 将对象序列化为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        Class<?> aClass = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(aClass); // 获取对象的schema
        try {
            // 使用Protostuff进行序列化
            return ProtobufIOUtil.toByteArray(obj, schema, BUFFER);
        }  finally {
            // 清理缓冲区，避免内存泄漏
            BUFFER.clear();
        }
    }

    /**
     * 将字节数组反序列化为指定类型的对象
     * @param bytes 字节数组
     * @param clazz 目标类型
     * @return 反序列化得到的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T message = schema.newMessage(); // 创建目标对象
        ProtobufIOUtil.mergeFrom(bytes, message, schema); // 填充数据
        return message;
    }
}

