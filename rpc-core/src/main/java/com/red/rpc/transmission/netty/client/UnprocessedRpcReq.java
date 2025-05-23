package com.red.rpc.transmission.netty.client;

import com.red.rpc.dto.RpcResp;
import com.red.rpc.exception.RpcException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的RPC请求管理类。
 * 用于客户端异步发送请求后，保存请求ID与CompletableFuture的映射，
 * 服务端响应到达时根据请求ID完成对应的future，实现请求-响应的异步解耦。
 */
public class UnprocessedRpcReq {
    /**
     * 存储请求ID与对应CompletableFuture的映射关系。
     * key为请求ID，value为等待响应的future。
     */
    private static final Map<String, CompletableFuture<RpcResp<?>>> RESP_CF_MAP = new ConcurrentHashMap<>();

    /**
     * 新增一个未完成的请求future。
     * @param requestId 请求ID
     * @param completableFuture 等待响应的future
     */
    public static void put(String requestId, CompletableFuture<RpcResp<?>> completableFuture) {
        RESP_CF_MAP.put(requestId, completableFuture);
    }

    /**
     * 根据响应对象完成对应的future，并移除映射关系。
     * 如果未找到对应future则抛出异常。
     * @param rpcResp 服务端返回的响应
     */
    public static void complete(RpcResp<?> rpcResp) {
        CompletableFuture<RpcResp<?>> cf = RESP_CF_MAP.remove(rpcResp.getReqId());
        if (Objects.isNull(cf)){
            throw new RpcException("没有找到对应的请求");
        }
        cf.complete(rpcResp);
    }
}
