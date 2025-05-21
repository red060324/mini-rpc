package com.red.rpc.transmission.netty.server;

import com.red.rpc.dto.RpcMsg;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("服务端接收到请求: {}", rpcMsg);
        RpcReq rpcReq = (RpcReq) rpcMsg.getData();

        RpcResp<String> rpcResp = RpcResp.success(rpcReq.getReqId(), "模拟响应数据");

        RpcMsg msg = RpcMsg.builder()
                .reqId(rpcMsg.getReqId())
                .msgType(MsgType.RPC_RESP)
                .compressType(CompressType.GZIP)
                .serializeType(SerializeType.KRYO)
                .version(VersionType.VERSION1)
                .data(rpcResp)
                .build();

        ctx.channel()
                .writeAndFlush(msg)
                .addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 处理异常
        log.error("服务端发生异常", cause);
        // 关闭连接
        ctx.close();
    }
}
