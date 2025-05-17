package com.red.rpc.provider.impl;

import cn.hutool.core.collection.CollUtil;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class SimpleServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    @Override
    public void publishService(RpcServiceConfig config) {
        //发布服务本质上是将 key为全类名版本号类型，value为实际实现的对象 的键值对放入map中，以便后续从其中拿出来
        List<String> rpcServiceNames = config.rpcServiceNames();
        if (CollUtil.isEmpty(rpcServiceNames)) {
            throw new RuntimeException("该服务没有实现接口");
        }
        log.debug("发布服务：{}",rpcServiceNames);
        rpcServiceNames.forEach(rpcServiceName -> {
            SERVICE_CACHE.put(rpcServiceName,config.getService());
        });

    }

    @Override
    public Object getService(String rpcServiceName) {
        if (!SERVICE_CACHE.containsKey(rpcServiceName)){
            throw new IllegalArgumentException("找不到对应服务:" + rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }
}
