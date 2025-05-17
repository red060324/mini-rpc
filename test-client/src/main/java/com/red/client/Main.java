package com.red.client;

import com.red.api.User;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcClient;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    public static void main(String[] args) {



    }

//    private static <T> T invoke(Long id){
//        RpcClient rpcClient;
//        RpcReq req = RpcReq.builder()
//                .reqId("123123")
//                .interfaceName("com.red.api.UserService")
//                .methodName("getUser")
//                .params(new Object[]{id})
//                .paramTypes(new Class[]{Long.class})
//                .build();
//        RpcResp<?> rpcResp = rpcClient.sendReq(req);
//        return (T) rpcResp.getData();
//
//
//    }
}
