package com.red.server;

import com.red.rpc.transmission.RpcServer;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    //启动之后，rpc server 就可以对外提供服务
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer() {
            @Override
            public void start() {

            }
        };
        rpcServer.start();
    }
}
