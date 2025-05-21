package com.red.rpc.transmission.netty.codec;

import com.red.rpc.compress.Compress;
import com.red.rpc.compress.impl.GzipCompress;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcMsg;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.serialize.Serializer;
import com.red.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty RPC消息编码器，将RpcMsg对象编码为二进制数据发送到网络。
 * 编码结构包括魔数、版本号、消息头、序列化类型、压缩类型、消息ID和消息体等。
 */
public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {

    /**
     * 编码方法，将RpcMsg对象编码为ByteBuf
     * @param channelHandlerContext Netty上下文
     * @param rpcMsg 待编码的RPC消息
     * @param byteBuf 输出缓冲区
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        // 1. 写入魔数，用于协议校验
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        // 2. 写入版本号
        byteBuf.writeByte(rpcMsg.getVersion().getCode());

        // 3. 预留4字节写入消息总长度（后续回填）
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        // 4. 写入消息类型
        byteBuf.writeByte(rpcMsg.getMsgType().getCode());

        // 5. 写入序列化类型
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());

        // 6. 写入压缩类型
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());

        // 7. 写入消息ID
        byteBuf.writeInt(rpcMsg.getReqId());

        // 8. 计算消息体长度并写入消息体
        int msgLen = RpcConstant.REQ_HEAD_LEN;
        // 非心跳消息且数据不为空时，序列化并压缩数据
        if (!rpcMsg.getMsgType().isHeartbeat() && !Objects.isNull(rpcMsg.getData())){
            byte[] data = dataToBytes(rpcMsg);
            byteBuf.writeBytes(data);
            msgLen += data.length;
        }
        // 9. 回填消息长度到预留位置
        int curIdx = byteBuf.writerIndex();
        byteBuf.writerIndex(curIdx - msgLen + RpcConstant.RPC_MAGIC_CODE.length + 1);
        byteBuf.writeInt(msgLen);
        byteBuf.writerIndex(curIdx);
    }

    /**
     * 将消息体对象序列化并压缩为字节数组
     * @param rpcMsg RPC消息
     * @return 序列化并压缩后的字节数组
     */
    private byte[] dataToBytes(RpcMsg rpcMsg) {
        // todo: 根据rpcMsg的序列化和压缩类型动态获取实现
        // 获取序列化器实例（默认Kryo）
        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] data = serializer.serialize(rpcMsg.getData());

        // 获取压缩器实例（默认Gzip）
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        data = compress.compress(data);
        
        return data;
    }
}
