package com.red.client.utils;

import com.red.rpc.proxy.RpcClientProxy;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.socket.client.SocketRpcClient;

/**
 * @author red
 * @date 2025/5/18
 * @description
 */
public class ProxyUtils {
    private static final RpcClient rpcClient = new SocketRpcClient("127.0.0.1",8888);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz){
        return rpcClientProxy.getProxy(clazz);
    }
}
