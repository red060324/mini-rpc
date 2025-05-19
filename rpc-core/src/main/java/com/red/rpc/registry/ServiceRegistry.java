package com.red.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress address);

}
