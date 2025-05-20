package com.red.client.nettytest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @author red
 * @date 2025/5/20
 * @description
 */
public class NettyClient {
    public static void main(String[] args) {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new MyClientHandler());
                    }
                })
                .connect("127.0.0.1", 8081).channel();

    }

    public static class MyClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //连接建立后向服务端发送消息每隔5秒发送一次
//            while (true){
//                ctx.writeAndFlush("hello world");
//                TimeUnit.SECONDS.sleep(5);
//            }

            // 连接建立后向服务端发送消息
            String msg = "client msg 1";
            System.out.println("客户端发送消息：" + msg);
            ctx.channel().writeAndFlush(msg);


        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("客户端收到服务端消息: " + msg);
        }
    }
}
