package com.red.rpc.transmission.socket.server;

import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.handler.RpcReqHandler;
import com.red.rpc.provider.ServiceProvider;
import com.red.rpc.provider.impl.SimpleServiceProvider;
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
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final RpcReqHandler rpcReqHandler;
    private final ServiceProvider serviceProvider;
    private final ExecutorService executor;

    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(int port) {
        this(port,new SimpleServiceProvider());
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        this.executor = ThreadPoolUtils.createIoIntensiveThreadPool("socket-rpc-server");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.debug("服务启动，端口：{}",port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null){
                executor.submit(new SocketReqHandler(socket,rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }


}
