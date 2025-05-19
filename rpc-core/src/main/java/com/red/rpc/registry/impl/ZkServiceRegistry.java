package com.red.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.registry.ServiceRegistry;
import com.red.rpc.registry.zk.ZkClient;
import com.red.rpc.util.IpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        log.info("服务注册，rpcServiceName：{}，address：{}", rpcServiceName, address);

        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH + rpcServiceName
                + StrUtil.SLASH + IpUtils.toIpPort(address);
        zkClient.createPersistentNode(path);
    }
}
