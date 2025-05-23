package com.red.rpc.transmission;

import com.red.rpc.config.RpcServiceConfig;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public interface RpcServer {
    void start();

    void publishService(RpcServiceConfig config);
}
