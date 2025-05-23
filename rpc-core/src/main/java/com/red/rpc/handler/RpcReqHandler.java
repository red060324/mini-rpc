package com.red.rpc.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.red.rpc.annotation.Limit;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.exception.RpcException;
import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class RpcReqHandler {
    private final ServiceProvider serviceProvider;
    private static final Map<String, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows
    public Object invoke(RpcReq rpcReq){
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        log.debug("获取到对应服务:{}",service.getClass().getCanonicalName());

        Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());

        Limit limit = method.getAnnotation(Limit.class);
        if (Objects.isNull(limit)){
            return method.invoke(service,rpcReq.getParams());
        }
        // 限流处理
        RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(rpcServiceName,
                key -> RateLimiter.create(limit.permitsPerSecond()));
        if (!rateLimiter.tryAcquire(limit.timeout(), TimeUnit.MILLISECONDS)){
            log.warn("限流失败，拒绝请求:{}",rpcReq);
            throw new RpcException("限流失败");
        }
        return method.invoke(service,rpcReq.getParams());
    }
}
