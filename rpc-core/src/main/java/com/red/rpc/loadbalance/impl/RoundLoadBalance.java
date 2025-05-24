package com.red.rpc.loadbalance.impl;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.loadbalance.LoadBalance;
import io.protostuff.Rpc;

import java.util.List;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
public class RoundLoadBalance implements LoadBalance {
    private int last = -1;

    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        last++;
        last = last % list.size();
        return list.get(last);
    }
}
