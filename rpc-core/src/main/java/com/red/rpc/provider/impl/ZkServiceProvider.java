package com.red.rpc.provider.impl;

import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.provider.ServiceProvider;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public class ZkServiceProvider implements ServiceProvider {
    @Override
    public void publishService(RpcServiceConfig config) {

    }

    @Override
    public Object getService(String rpcServiceName) {
        return null;
    }
}
