package com.red.client;

import com.red.api.User;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.socket.client.SocketRpcClient;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    public static void main(String[] args) {


        RpcClient rpcClient = new SocketRpcClient("127.0.0.1",8888);
        RpcReq req = RpcReq.builder()
                .reqId("123123")
                .interfaceName("com.red.api.UserService")
                .methodName("getUser")
                .params(new Object[]{1L})
                .paramTypes(new Class[]{Long.class})
                .build();
        RpcResp<?> rpcResp = rpcClient.sendReq(req);
        System.out.println(rpcResp.getData());


    }


}
