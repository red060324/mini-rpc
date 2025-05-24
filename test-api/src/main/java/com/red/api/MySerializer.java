package com.red.api;

import com.red.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
@Slf4j
public class MySerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            log.info("=========使用MySerializer序列化========="); // 日志输出
            oos.writeObject(obj); // 将对象写入字节数组输出流
            oos.flush(); // 刷新输出流
            return baos.toByteArray(); // 返回字节数组
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e); // 抛出运行时异常
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        // 反序列化逻辑
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bais)) {
            log.info("=========使用MySerializer反序列化========="); // 日志输出
            return clazz.cast(ois.readObject());
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e); // 抛出运行时异常
        }
    }
}
