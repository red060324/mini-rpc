package com.red.rpc.loadbalance.impl;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.loadbalance.LoadBalance;
import org.apache.curator.shaded.com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
public class ConsistentHashLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list,RpcReq rpcReq) {
        String key = rpcReq.rpcServiceName();
        // 计算 hash 值
        long hashCode = Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asLong();

        int index = Hashing.consistentHash(hashCode, list.size());



        return list.get(index);
    }
}
