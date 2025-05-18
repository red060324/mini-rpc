package com.red.server;

import com.red.api.User;
import com.red.api.UserService;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.proxy.RpcClientProxy;
import com.red.rpc.transmission.RpcServer;
import com.red.rpc.transmission.socket.server.SocketRpcServer;
import com.red.rpc.util.ShutdownHookUtils;
import com.red.server.service.UserServiceImpl;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    //启动之后，rpc server 就可以对外提供服务
    public static void main(String[] args) {
        RpcServer rpcServer = new SocketRpcServer(8888);
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());
        rpcServer.publishService(config);
        rpcServer.start();
        ShutdownHookUtils.clearAll();



    }
}
