package com.red.rpc.provider;

import com.red.rpc.config.RpcServiceConfig;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
public interface ServiceProvider {
    void publishService(RpcServiceConfig config);

    Object getService(String rpcServiceName);
}
