package com.red.rpc.registry;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;

import java.net.InetSocketAddress;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcReq rpcReq);
}
