package com.red.rpc.transmission.netty.client;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    /**
     * 服务发现组件，用于根据请求查找服务端地址
     */
    private final ServiceDiscovery serviceDiscovery;

    /**
     * Channel连接池，复用与服务端的连接
     */
    private ChannelPool channelPool;

    /**
     * 默认构造方法，使用Zookeeper服务发现
     */
    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    /**
     * 支持自定义服务发现实现的构造方法
     * @param serviceDiscovery 服务发现实现
     */
    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.channelPool = SingletonFactory.getInstance(ChannelPool.class);
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
                        // 添加空闲检测、解码器、编码器和客户端业务处理器到pipeline
                        ch.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new NettyRpcDecoder());
                        ch.pipeline().addLast(new NettyRpcEncoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    /**
     * 发送RPC请求到服务端，并同步等待响应
     *
     * @param req RPC请求对象
     * @return RPC响应对象
     */
    @SneakyThrows
    @Override
    public Future<RpcResp<?>> sendReq(RpcReq req) {

        // 创建一个CompletableFuture用于异步接收响应
        CompletableFuture<RpcResp<?>> cf = new CompletableFuture<>();
        // 将请求ID与future绑定，响应到来时由handler完成future
        UnprocessedRpcReq.put(req.getReqId(), cf);

        // 通过服务发现获取服务端地址
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        // 1. 建立与服务端的连接，优先复用连接池中的Channel
        Channel channel = channelPool.get(address,() -> connect(address));
        log.info("客户端连接成功，远程地址: {}", channel.remoteAddress());

        // 2. 构造RpcMsg消息对象，包含版本、类型、序列化、压缩、请求ID和数据
        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .msgType(MsgType.RPC_REQ)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .data(req)
                .build();

        // 3. 发送消息到服务端，发送失败时关闭连接并通知future异常
        channel.writeAndFlush(rpcMsg).addListener((ChannelFutureListener) listener ->{
            if (!listener.isSuccess()){
                listener.channel().close();
                cf.completeExceptionally(listener.cause());
            }
        });

        // 4. 阻塞等待CompletableFuture响应结果返回
        return cf;
    }

    /**
     * 建立与指定服务端的连接
     * @param address 服务端地址
     * @return 可用的Channel
     */
    private Channel connect(InetSocketAddress address) {
        try {
            // 连接远程服务端，返回Channel
            return bootstrap.connect(address).sync().channel();
        } catch (InterruptedException e) {
            log.error("连接服务端失败", e);
            throw new RuntimeException(e);
        }
    }
}
