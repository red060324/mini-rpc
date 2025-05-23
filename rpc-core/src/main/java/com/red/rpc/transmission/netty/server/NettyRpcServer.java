package com.red.rpc.transmission.netty.server;

import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.provider.ServiceProvider;
import com.red.rpc.provider.impl.ZkServiceProvider;
import com.red.rpc.registry.ServiceDiscovery;
import com.red.rpc.transmission.RpcServer;
import com.red.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.red.rpc.transmission.netty.codec.NettyRpcEncoder;
import com.red.rpc.util.ShutdownHookUtils;
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
    private final ServiceProvider serviceProvider;
    private final int port;

    public NettyRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(int port) {
        this(SingletonFactory.getInstance(ZkServiceProvider.class), port);
    }

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this(serviceProvider,RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(ServiceProvider serviceProvider, int port) {
        this.serviceProvider = serviceProvider;
        this.port = port;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyRpcDecoder());
                            ch.pipeline().addLast(new NettyRpcEncoder());
                            ch.pipeline().addLast(new NettyRpcServerHandler(serviceProvider));
                        }
                    });
            ShutdownHookUtils.clearAll();
            Channel channel = bootstrap.bind(port).sync().channel();
            log.info("服务端启动成功，端口: {}", port);
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
        serviceProvider.publishService(config);
    }
}
