package com.red.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.registry.ServiceRegistry;
import com.red.rpc.registry.zk.ZkClient;
import com.red.rpc.util.IpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 基于 Zookeeper 的服务注册实现类
 * 负责将服务信息注册到 Zookeeper 注册中心
 * @author red
 * @date 2025/5/19
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    // Zookeeper 客户端实例
    private final ZkClient zkClient;

    /**
     * 默认构造方法，使用单例工厂获取 ZkClient 实例
     */
    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    /**
     * 构造方法，允许传入自定义的 ZkClient 实例
     * @param zkClient Zookeeper 客户端
     */
    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * 注册服务到 Zookeeper
     * @param rpcServiceName 服务名称
     * @param address 服务提供者的地址（IP+端口）
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        // 打印服务注册日志
        log.info("服务注册，rpcServiceName：{}，address：{}", rpcServiceName, address);

        // 构建 Zookeeper 节点路径：/rpc/{服务名}/{ip:port}
        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH + rpcServiceName
                + StrUtil.SLASH + IpUtils.toIpPort(address);
        // 创建持久化节点，实现服务注册
        zkClient.createPersistentNode(path);
    }

    @SneakyThrows
    @Override
    public void clearAll() {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;
        zkClient.clearAll(new InetSocketAddress(host,port));
    }
}

