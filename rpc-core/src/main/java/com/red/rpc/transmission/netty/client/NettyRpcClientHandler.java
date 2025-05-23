package com.red.rpc.transmission.netty.client;

import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcMsg;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty RPC客户端处理器。
 * 负责处理服务端返回的RpcMsg消息，包括业务响应和心跳包，
 * 并将响应结果通知给未完成的请求future。
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    /**
     * 处理服务端返回的RpcMsg消息。
     * 如果是心跳包则记录日志并返回；
     * 如果是业务响应则通过UnprocessedRpcReq完成对应future。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType().isHeartbeat()){
            log.debug("客户端接收到心跳包: {}", rpcMsg);
            return;
        }
        log.debug("客户端接收到响应: {}", rpcMsg);
        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();
        // 将响应结果通知给等待的future
        UnprocessedRpcReq.complete(rpcResp);
        // 可选：收到响应后关闭连接（通常用于短连接场景）
//        ctx.channel().close();
    }

    /**
     * 处理用户事件，如空闲检测。
     * 当写空闲时，自动发送心跳包到服务端，保持连接活跃。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedHeartBeat = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;
        if (!isNeedHeartBeat){
            super.userEventTriggered(ctx, evt);
        }
        log.debug("客户端发送心跳包");
        ctx.writeAndFlush(RpcMsg.builder()
                .msgType(MsgType.HEARTBEAT_REQ)
                .compressType(CompressType.GZIP)
                .serializeType(SerializeType.KRYO)
                .version(VersionType.VERSION1)
                .build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    /**
     * 处理异常事件，记录日志并关闭连接。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生异常", cause);
        ctx.close();
    }
}
