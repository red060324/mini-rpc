package com.red.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.red.rpc.config.RpcServiceConfig;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.enums.RpcRespStatus;
import com.red.rpc.exception.RpcException;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.socket.client.SocketRpcClient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Rpc客户端代理类，用于创建远程服务的代理对象并处理方法调用。
 * @author red
 * @date 2025/5/18
 */
public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient; // RPC客户端实例，负责与远程服务器通信
    private final RpcServiceConfig config; // 配置信息，包含版本、分组等

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
     * 当通过代理对象调用接口方法时，此方法会被拦截并执行。
     * 创建请求对象，发送给远程服务器，并对响应进行校验。
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 远程调用返回的结果
     * @throws Throwable 如果发生异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 创建RPC客户端实例（此处可以优化为复用已有客户端）
        //RpcClient rpcClient = new SocketRpcClient("127.0.0.1", 8888);

        // 构建RPC请求对象
        RpcReq rpcReq = RpcReq.builder()
                .reqId(IdUtil.fastSimpleUUID()) // 生成唯一请求ID
                .interfaceName(method.getDeclaringClass().getCanonicalName()) // 接口全限定名
                .methodName(method.getName()) // 方法名
                .params(args) // 参数列表
                .paramTypes(method.getParameterTypes()) // 参数类型列表
                .version(config.getVersion()) // 版本号
                .group(config.getGroup()) // 分组信息
                .build();

        // 发送请求并接收响应
        RpcResp<?> rpcResp = rpcClient.sendReq(rpcReq);

        // 校验响应结果
        check(rpcReq, rpcResp);

        // 返回响应数据
        return rpcResp.getData();
    }

    /**
     * 校验RPC响应是否合法。
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