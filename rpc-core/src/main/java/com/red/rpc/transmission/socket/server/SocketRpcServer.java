package com.red.rpc.transmission.socket.server;

import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.handler.RpcReqHandler;
import com.red.rpc.provider.ServiceProvider;
import com.red.rpc.provider.impl.SimpleServiceProvider;
import com.red.rpc.provider.impl.ZkServiceProvider;
import com.red.rpc.transmission.RpcServer;
import com.red.rpc.util.ThreadPoolUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * 基于 Socket 的 RPC 服务端实现
 * 负责监听端口、接收客户端请求并分发处理
 * 支持多线程并发处理请求
 * @author red
 * @date 2025/5/17
 */
@Slf4j
public class SocketRpcServer implements RpcServer {
    /** 服务端监听端口 */
    private final int port;
    /** RPC 请求处理器 */
    private final RpcReqHandler rpcReqHandler;
    /** 服务提供者（负责服务的发布与获取） */
    private final ServiceProvider serviceProvider;
    /** 线程池，用于处理客户端请求 */
    private final ExecutorService executor;

    /**
     * 默认构造方法，使用默认端口
     */
    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    /**
     * 指定端口的构造方法，使用默认服务提供者
     * @param port 监听端口
     */
    public SocketRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZkServiceProvider.class));
    }

    /**
     * 完整构造方法，允许自定义端口和服务提供者
     * @param port 监听端口
     * @param serviceProvider 服务提供者
     */
    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        // 初始化请求处理器
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        // 创建 IO 密集型线程池
        this.executor = ThreadPoolUtils.createIoIntensiveThreadPool("socket-rpc-server-");
    }

    /**
     * 启动 RPC 服务端，监听端口并异步处理客户端请求
     */
    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.debug("服务启动，端口：{}",port);
            Socket socket;
            // 持续监听客户端连接
            while ((socket = serverSocket.accept()) != null){
                // 每个连接交由线程池异步处理
                executor.submit(new SocketReqHandler(socket,rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }

    /**
     * 发布服务到注册中心
     * @param config 服务配置对象
     */
    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }


}
