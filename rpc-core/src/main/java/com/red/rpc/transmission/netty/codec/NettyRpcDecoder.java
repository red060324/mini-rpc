package com.red.rpc.transmission.netty.codec;

import cn.hutool.core.util.ArrayUtil;
import com.red.rpc.compress.Compress;
import com.red.rpc.compress.impl.GzipCompress;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcMsg;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import com.red.rpc.exception.RpcException;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.serialize.Serializer;
import com.red.rpc.serialize.impl.KryoSerializer;
import com.red.rpc.spi.CustomLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

/**
 * Netty RPC解码器，负责将网络字节流还原为RpcMsg对象。
 * 继承LengthFieldBasedFrameDecoder，自动处理粘包/半包问题。
 * 解码流程包括：校验魔法值、读取协议头、解压缩、反序列化等。
 * @author red
 * @date 2025/5/21
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * 构造方法，设置帧解码参数
     */
    public NettyRpcDecoder() {
        // maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip
        super(RpcConstant.REQ_MAX_LEN,5,4,-9,0);
    }

    /**
     * 解码入口，先由父类处理帧边界，再自定义解码
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        return decodeFrame(frame);
    }

    /**
     * 解析完整帧，组装RpcMsg对象
     * @param byteBuf 完整的消息帧
     * @return RpcMsg对象
     */
    private Object decodeFrame(ByteBuf byteBuf){
        // 校验魔法值，防止非法数据包
        readAndCheckMagicCode(byteBuf);

        // 读取协议版本
        byte versionCode = byteBuf.readByte();
        VersionType version = VersionType.from(versionCode);

        // 读取消息体长度
        int msgLen = byteBuf.readInt();

        // 读取消息类型
        byte msgCode = byteBuf.readByte();
        MsgType msgType = MsgType.from(msgCode);

        // 读取序列化类型
        byte serializeCode = byteBuf.readByte();
        SerializeType serializeType = SerializeType.from(serializeCode);

        // 读取压缩类型
        byte compressCode = byteBuf.readByte();
        CompressType compressType = CompressType.from(compressCode);

        // 读取请求ID
        int reqId = byteBuf.readInt();

        // 读取并反序列化数据体
        Object data = readData(byteBuf, msgLen - RpcConstant.REQ_HEAD_LEN, msgType, serializeType);

        // 构建RpcMsg对象
        return RpcMsg.builder()
                .reqId(reqId)
                .msgType(msgType)
                .version(version)
                .serializeType(serializeType)
                .compressType(compressType)
                .data(data)
                .build();
    }

    /**
     * 校验魔法值，确保协议合法
     */
    private void readAndCheckMagicCode(ByteBuf byteBuf){
        byte[] magicBytes = new byte[RpcConstant.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);
        if (!ArrayUtil.equals(magicBytes,RpcConstant.RPC_MAGIC_CODE)){
            throw new RpcException("魔法值异常" + new String(magicBytes));
        }
    }

    /**
     * 根据消息类型读取并反序列化数据体
     * @param byteBuf 数据缓冲区
     * @param dataLen 数据长度
     * @param msgType 消息类型
     * @param serializeType 序列化类型
     * @return 反序列化后的对象
     */
    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType, SerializeType serializeType) {
        if (msgType.isReq()) {
            return readData(byteBuf, dataLen, RpcReq.class, serializeType);
        }
        return readData(byteBuf, dataLen, RpcResp.class, serializeType);
    }

    /**
     * 读取数据体并解压缩、反序列化为指定类型
     * @param byteBuf 数据缓冲区
     * @param dataLen 数据长度
     * @param clazz 目标类型
     * @param serializeType 序列化类型
     * @return 反序列化后的对象
     */
    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz, SerializeType serializeType) {
        if (dataLen <= 0){
            return null;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        // 解压缩数据
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        data = compress.decompress(data);

        // 获取序列化器并反序列化
        String serializeTypeStr = serializeType.getDesc();
        Serializer serializer = CustomLoader.getLoader(Serializer.class).get(serializeTypeStr);

        return serializer.deserialize(data,clazz);
    }
}
