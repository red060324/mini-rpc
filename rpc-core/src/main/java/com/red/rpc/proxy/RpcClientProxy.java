package com.red.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.red.rpc.annotation.Breaker;
import com.red.rpc.annotation.Retry;
import com.red.rpc.breaker.CircuitBreak;
import com.red.rpc.breaker.CircuitBreakManager;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.RpcRespStatus;
import com.red.rpc.exception.RpcException;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.socket.client.SocketRpcClient;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Rpc客户端代理类，用于创建远程服务的代理对象并处理方法调用。
 * 通过JDK动态代理，将本地方法调用转换为RPC请求，发送到远程服务端，并返回结果。
 * 支持自定义服务配置（如版本、分组）。
 * @date 2025/5/18
 */
public class RpcClientProxy implements InvocationHandler {
    /**
     * RPC客户端实例，负责与远程服务器通信
     */
    private final RpcClient rpcClient;
    /**
     * 服务配置信息，包含版本、分组等
     */
    private final RpcServiceConfig config;

    /**
     * 构造函数，初始化RpcClientProxy实例，默认使用空配置。
     *
     * @param rpcClient RPC客户端实例
     */
    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig());
    }

    /**
     * 构造函数，初始化RpcClientProxy实例，允许自定义配置。
     *
     * @param rpcClient RPC客户端实例
     * @param config    自定义的服务配置
     */
    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    /**
     * 获取指定接口的代理对象。
     * 通过JDK动态代理，将接口方法调用转为RPC请求。
     *
     * @param clazz 接口类
     * @param <T>   接口类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }

    /**
     * 拦截代理对象的方法调用，构造RPC请求并发送到远程服务端，返回响应结果。
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 远程调用返回的结果
     * @throws Throwable 如果发生异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建RPC请求对象，包含接口名、方法名、参数、版本、分组等信息
        RpcReq rpcReq = RpcReq.builder()
                .reqId(IdUtil.fastSimpleUUID()) // 生成唯一请求ID
                .interfaceName(method.getDeclaringClass().getCanonicalName()) // 接口全限定名
                .methodName(method.getName()) // 方法名
                .params(args) // 参数列表
                .paramTypes(method.getParameterTypes()) // 参数类型列表
                .version(config.getVersion()) // 版本号
                .group(config.getGroup()) // 分组信息
                .build();

        Breaker breaker = method.getAnnotation(Breaker.class);
        if (Objects.isNull(breaker)){
            return sendReqWithRetry(rpcReq, method);
        }

        CircuitBreak circuitBreak = CircuitBreakManager.get(rpcReq.rpcServiceName(), breaker);
        if (!circuitBreak.canReq()) {
            throw new RpcException("熔断器打开，拒绝请求");
        }
        try {
            Object o = sendReqWithRetry(rpcReq, method);
            circuitBreak.success();
            return o;
        } catch (RpcException e) {
            circuitBreak.fail();
            throw e;
        }

    }

    @SneakyThrows
    private Object sendReqWithRetry(RpcReq rpcReq, Method method) {
        Retry retry = method.getAnnotation(Retry.class);
        if (Objects.isNull(retry)){
            return sendReq(rpcReq);
        }

        Retryer<Object> retryer = RetryerBuilder.<Object>newBuilder()
                .retryIfExceptionOfType(retry.value())
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry.maxAttempts()))
                .withWaitStrategy(WaitStrategies.fixedWait(retry.delay(), TimeUnit.MILLISECONDS))
                .build();
        return retryer.call(() -> sendReq(rpcReq));
    }

    @SneakyThrows
    private Object sendReq(RpcReq rpcReq){
        // 发送请求并接收响应（异步等待）
        Future<RpcResp<?>> future = rpcClient.sendReq(rpcReq);
        RpcResp<?> rpcResp = future.get();

        // 校验响应结果
        check(rpcReq, rpcResp);

        // 返回响应数据
        return rpcResp.getData();
    }

    /**
     * 校验RPC响应是否合法。
     * 检查响应是否为空、请求ID是否一致、响应状态是否成功。
     *
     * @param rpcReq  请求对象
     * @param rpcResp 响应对象
     * @throws RpcException 如果校验失败
     */
    private void check(RpcReq rpcReq, RpcResp<?> rpcResp) {
        if (Objects.isNull(rpcResp)) {
            throw new RpcException("rpcResp为空");
        }

        if (!Objects.equals(rpcReq.getReqId(), rpcResp.getReqId())) {
            throw new RpcException("请求和响应id不一致");
        }

        if (RpcRespStatus.isFailed(rpcResp.getCode())) {
            throw new RpcException("响应值为失败：" + rpcResp.getMsg());
        }
    }
}
