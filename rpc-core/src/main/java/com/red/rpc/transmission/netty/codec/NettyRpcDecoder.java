package com.red.rpc.transmission.netty.codec;

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
        this(0,0,0);
    }

    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
