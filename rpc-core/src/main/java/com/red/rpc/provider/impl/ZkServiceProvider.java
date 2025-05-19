package com.red.rpc.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.provider.ServiceProvider;
import com.red.rpc.registry.ServiceRegistry;
import com.red.rpc.registry.impl.ZkServiceRegistry;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 Zookeeper 的服务提供者实现类
 * 负责服务的发布与获取
 * author red
 * @date 2025/5/19
 */
public class ZkServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();
    private final ServiceRegistry serviceRegistry;


    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 发布服务到注册中心
     * @param config 服务配置对象，包含服务相关信息
     */
    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames().forEach(rpcServiceName -> {
            publishService(rpcServiceName,config.getService());
        });
    }

    /**
     * 根据服务名称获取服务实例
     * @param rpcServiceName 服务名称
     * @return 服务实例对象，未找到返回 null
     */
    @Override
    public Object getService(String rpcServiceName) {
        if (StrUtil.isBlank(rpcServiceName)){
            throw new IllegalArgumentException("服务名称不能为空");
        }
        // 检查缓存中是否存在该服务实例
        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new IllegalArgumentException("找不到对应服务:" + rpcServiceName);
        }
        // 如果存在，直接从缓存中取出并返回
        return SERVICE_CACHE.get(rpcServiceName);
    }

    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        // 将服务注册到注册中心
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;
        InetSocketAddress address = new InetSocketAddress(host, port);
        serviceRegistry.registerService(rpcServiceName, address);
        // 将服务实例缓存到本地
        SERVICE_CACHE.put(rpcServiceName, service);
    }
}
