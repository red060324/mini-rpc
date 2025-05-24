package com.red.rpc.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.red.rpc.exception.RpcException;
import com.red.rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化实现，基于Hessian二进制协议进行对象的序列化与反序列化。
 * 适用于网络传输和跨语言通信。
 * @author red
 * @date 2025/5/24
 */
public class HessianSerializer implements Serializer {
    /**
     * 将对象序列化为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(baos);
            hessianOutput.writeObject(obj); // 写入对象
            hessianOutput.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            // 序列化失败时抛出自定义异常
            throw new RpcException(e);
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
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(bais);
            Object o = hessianInput.readObject(); // 读取对象
            return clazz.cast(o);
        } catch (IOException e) {
            // 反序列化失败时抛出自定义异常
            throw new RpcException(e);
        }
    }
}

