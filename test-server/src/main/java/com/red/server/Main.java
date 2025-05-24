package com.red.server;

import cn.hutool.core.collection.ListUtil;
import com.red.api.User;
import com.red.api.UserService;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.loadbalance.LoadBalance;
import com.red.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.red.rpc.loadbalance.impl.RandomLoadBalance;
import com.red.rpc.loadbalance.impl.RoundLoadBalance;
import com.red.rpc.proxy.RpcClientProxy;
import com.red.rpc.serialize.Serializer;
import com.red.rpc.serialize.impl.HessianSerializer;
import com.red.rpc.serialize.impl.ProtostuffSerializer;
import com.red.rpc.transmission.RpcServer;
import com.red.rpc.transmission.netty.server.NettyRpcServer;
import com.red.rpc.transmission.socket.server.SocketRpcServer;
import com.red.rpc.util.ShutdownHookUtils;
import com.red.server.service.UserServiceImpl;

import java.util.List;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    //启动之后，rpc server 就可以对外提供服务
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyRpcServer();

        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());
        rpcServer.publishService(config);

        rpcServer.start();

    }
}
