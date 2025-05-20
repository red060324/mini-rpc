package com.red.rpc.transmission.netty.client;

import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    static {
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        Channel channel = bootstrap.connect("127.0.0.1", RpcConstant.SERVER_PORT)
                .sync()
                .channel();
        log.info("客户端连接成功，远程地址: {}", channel.remoteAddress());
        String interfaceName = req.getInterfaceName();
        channel.writeAndFlush(interfaceName).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        //阻塞等待，直到关闭
        channel.closeFuture().sync();
        //获取响应
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        String s = channel.attr(key).get();
        System.out.println(s);
        return null;
    }
}
