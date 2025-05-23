package com.red.rpc.transmission.netty.server;

import com.red.rpc.dto.RpcMsg;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.handler.RpcReqHandler;
import com.red.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

    private final RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    public NettyRpcServerHandler(RpcReqHandler rpcReqHandler) {
        this.rpcReqHandler = rpcReqHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("服务端接收到请求: {}", rpcMsg);

        // 区分心跳请求和业务请求
        MsgType msgType;
        Object data;
        if (rpcMsg.getMsgType().isHeartbeat()){
            msgType = MsgType.HEARTBEAT_RESP;
            data = null;
        } else {
            RpcReq rpcReq = (RpcReq) rpcMsg.getData();
            msgType = MsgType.RPC_RESP;
            data = handleRpcReq(rpcReq);
        }

        // 构建响应消息
        RpcMsg msg = RpcMsg.builder()
                .msgType(msgType)
                .compressType(CompressType.GZIP)
                .serializeType(SerializeType.KRYO)
                .version(VersionType.VERSION1)
                .data(data)
                .build();

        ctx.channel()
                .writeAndFlush(msg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 处理异常
        log.error("服务端发生异常", cause);
        // 关闭连接
        ctx.close();
    }

    private RpcResp<?> handleRpcReq(RpcReq rpcReq) {
        // 处理请求
        try {
            Object object = rpcReqHandler.invoke(rpcReq);
            return RpcResp.success(rpcReq.getReqId(), object);
        } catch (Exception e) {
            log.info("调用失败", e);
            return RpcResp.fail(rpcReq.getReqId(), e.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedClose = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;
        if (!isNeedClose){
            super.userEventTriggered(ctx, evt);
        }
        log.debug("服务端长时间未接收到请求，关闭 channel :{}", ctx.channel().remoteAddress());
        ctx.close();
    }

}
