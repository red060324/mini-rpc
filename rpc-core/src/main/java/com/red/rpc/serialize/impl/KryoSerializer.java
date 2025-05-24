package com.red.rpc.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
@Slf4j
public class KryoSerializer implements Serializer {
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        // 注册需要序列化的类
        kryo.register(RpcReq.class);
        kryo.register(RpcResp.class);
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream();Output output = new Output(oos)) {
            // 获取当前线程的Kryo实例
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 序列化对象,把obj变成byte数组写入到output流中
            kryo.writeObject(output, obj);
            output.flush();
            log.info("=========使用Kryo序列化=========");

            return oos.toByteArray();
        } catch (Exception e) {
            log.error("Kryo序列化失败", e);
            throw new RuntimeException(e);
        }finally {
            // 清理ThreadLocal中的Kryo实例
            KRYO_THREAD_LOCAL.remove();
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
            // 获取当前线程的Kryo实例
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            log.info("=========使用Kryo反序列化=========");

            // 反序列化对象
            return kryo.readObject(input, clazz);
        } catch (IOException e) {
            log.error("Kryo反序列化失败", e);
            throw new RuntimeException(e);
        }finally {
            // 清理ThreadLocal中的Kryo实例
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
