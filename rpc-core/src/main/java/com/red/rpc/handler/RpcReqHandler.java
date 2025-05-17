package com.red.rpc.handler;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class RpcReqHandler {
    private final ServiceProvider serviceProvider;

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows
    public Object invoke(RpcReq rpcReq){
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        log.debug("获取到对应服务:{}",service.getClass().getCanonicalName());

        Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());

        return method.invoke(service,rpcReq.getParams());
    }
}
