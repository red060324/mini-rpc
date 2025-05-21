package com.red.rpc.transmission.netty.server;

import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.transmission.RpcServer;
import com.red.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.red.rpc.transmission.netty.codec.NettyRpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
@Slf4j
public class NettyRpcServer implements RpcServer {

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Channel channel = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyRpcDecoder());
                            ch.pipeline().addLast(new NettyRpcEncoder());
                            ch.pipeline().addLast(new NettyRpcServerHandler());
                        }
                    })
                    .bind(RpcConstant.SERVER_PORT).sync().channel();
            log.info("服务端启动成功，端口: {}", RpcConstant.SERVER_PORT);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端异常", e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {

    }
}
