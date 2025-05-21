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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    public NettyRpcDecoder() {
        super(RpcConstant.REQ_MAX_LEN,5,4,-9,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        return decodeFrame(frame);
    }

    private Object decodeFrame(ByteBuf byteBuf){
        readAndCheckMagicCode(byteBuf);

        byte versionCode = byteBuf.readByte();
        VersionType version = VersionType.from(versionCode);

        int msgLen = byteBuf.readInt();

        byte msgCode = byteBuf.readByte();
        MsgType msgType = MsgType.from(msgCode);

        byte serializeCode = byteBuf.readByte();
        SerializeType serializeType = SerializeType.from(serializeCode);

        byte compressCode = byteBuf.readByte();
        CompressType compressType = CompressType.from(compressCode);

        int reqId = byteBuf.readInt();


        Object data = readData(byteBuf, msgLen - RpcConstant.REQ_HEAD_LEN, msgType);

        return RpcMsg.builder()
                .reqId(reqId)
                .msgType(msgType)
                .version(version)
                .serializeType(serializeType)
                .compressType(compressType)
                .data(data)
                .build();
    }

    private void readAndCheckMagicCode(ByteBuf byteBuf){
        byte[] magicBytes = new byte[RpcConstant.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);
        if (!ArrayUtil.equals(magicBytes,RpcConstant.RPC_MAGIC_CODE)){
            throw new RpcException("魔法值异常" + new String(magicBytes));
        }
    }

    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType) {
        if (msgType.isReq()) {
            return readData(byteBuf, dataLen, RpcReq.class);
        }
        return readData(byteBuf, dataLen, RpcResp.class);


    }

    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz){
        if (dataLen <= 0){
            return null;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        data = compress.decompress(data);

        KryoSerializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        return serializer.deserialize(data,clazz);

    }
}
