package com.red.rpc.transmission.netty.server;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rpcReq) throws Exception {
        log.debug("服务端接收到请求: {}", rpcReq);

//        RpcResp<String> rpcResp = RpcResp.success(rpcReq, "模拟响应数据");
        ctx.channel()
                .writeAndFlush("模拟响应数据")
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
