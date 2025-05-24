package com.red.rpc.loadbalance;

import com.red.rpc.dto.RpcReq;

import java.util.List;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public interface LoadBalance {
    String select(List<String> list,RpcReq rpcReq);
}
