package com.red.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.loadbalance.LoadBalance;
import com.red.rpc.loadbalance.impl.RandomLoadBalance;
import com.red.rpc.registry.ServiceDiscovery;
import com.red.rpc.registry.zk.ZkClient;
import com.red.rpc.util.IpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final LoadBalance loadBalance;
    /**
     * Zookeeper 服务发现
     * 1. 连接 zk
     * 2. 获取服务列表
     * 3. 随机选择一个服务
     */
    public ZkServiceDiscovery() {
        this(SingletonFactory.getInstance(ZkClient.class),
                SingletonFactory.getInstance(RandomLoadBalance.class));
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcReq rpcReq) {
        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcReq.rpcServiceName();
        List<String> childrenNode = zkClient.getChildrenNode(path);
        String address = loadBalance.select(childrenNode);

        return IpUtils.toInetSocketAddress(address);
    }
}
