package com.red.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcServiceConfig {
    /**
     * 通过传过来的version, group, service参数，可以找到唯一的对象，从而去调他的方法
     */
    private String version = "";
    private String group = "";
    private Object service;

    public RpcServiceConfig(Object service) {
        this.service = service;
    }

    public List<String> rpcServiceNames(){
        return interfaceNames().stream()
                .map(interfaceName -> interfaceName + getVersion() + getGroup())
                .collect(Collectors.toList());
    }

    //传进来的服务的实现的接口名，可以有很多
    private List<String> interfaceNames(){
        return Arrays.stream(service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());

    }
}
