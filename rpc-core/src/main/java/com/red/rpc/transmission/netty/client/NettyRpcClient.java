package com.red.rpc.transmission.netty.client;

import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcMsg;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.registry.ServiceDiscovery;
import com.red.rpc.registry.impl.ZkServiceDiscovery;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.red.rpc.transmission.netty.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty RPC客户端实现，负责通过Netty与服务端建立连接并发送RPC请求。
 * 支持消息的序列化、压缩和自动生成唯一请求ID。
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    /**
     * Netty客户端启动引导类，负责配置和初始化客户端相关参数。
     */
    private static final Bootstrap bootstrap;
    /**
     * 默认连接超时时间（毫秒）
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    static {
        // 初始化Netty Bootstrap，配置事件循环组、通道类型、日志处理器、超时等参数
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加解码器、编码器和客户端业务处理器到pipeline
                        ch.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new NettyRpcDecoder());
                        ch.pipeline().addLast(new NettyRpcEncoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    /**
     * 发送RPC请求到服务端，并同步等待响应
     * @param req RPC请求对象
     * @return RPC响应对象
     */
    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {

        InetSocketAddress address = serviceDiscovery.lookupService(req);


        // 1. 建立与服务端的连接
        Channel channel = bootstrap.connect(address)
                .sync()
                .channel();
        log.info("客户端连接成功，远程地址: {}", channel.remoteAddress());

        // 2. 构造RpcMsg消息对象，包含版本、类型、序列化、压缩、请求ID和数据
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .msgType(MsgType.RPC_REQ)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .data(req)
                .build();

        // 3. 发送消息到服务端，发送失败时关闭连接
        channel.writeAndFlush(rpcMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        // 4. 阻塞等待通道关闭（即收到响应后关闭）
        channel.closeFuture().sync();

        // 5. 从通道属性中获取响应结果
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        return channel.attr(key).get();
    }
}
