package com.red.server.nettytest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
public class NettyServer {
    public static void main(String[] args) {
        // 1. 创建两个线程组，bossGroup 用于接收客户端的连接，workerGroup 用于处理客户端的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                // 2. 设置服务器的通道类型
                .channel(NioServerSocketChannel.class)
                // 3. 设置服务器的处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new MyServerHandler());

                    }
                })
                .bind(8081);
    }

    public static class MyServerHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("服务端收到客户端消息: " + msg);

            // 发送消息给客户端
            String sendMsg = "server msg 1";
            System.out.println("服务端发送消息：" + sendMsg);
            ctx.channel().writeAndFlush(sendMsg);
        }
    }
}
