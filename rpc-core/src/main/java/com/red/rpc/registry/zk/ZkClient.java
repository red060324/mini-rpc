package com.red.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.exception.RpcException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * Zookeeper 客户端工具类，封装了 CuratorFramework 的基本操作
 * 用于连接 Zookeeper、创建节点、获取子节点等功能
 * @author red
 * @date 2025/5/19
 */
@Slf4j
public class ZkClient {
    // CuratorFramework 客户端实例
    private CuratorFramework client;
    // 重试之间等待的初始时间（毫秒）
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;

    /**
     * 默认构造方法，使用常量中的 zk 地址和端口
     */
    public ZkClient(){
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    /**
     * 构造方法，指定 zk 地址和端口
     * @param hostName zk 服务端地址
     * @param port zk 服务端端口
     */
    public ZkClient(String hostName, int port){
        // 创建重试策略：初始等待 1s，最多重试 3 次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        // 构建 CuratorFramework 客户端
        client = CuratorFrameworkFactory.builder()
                // 要连接的服务器列表
                .connectString(hostName + StrUtil.COLON + port)
                .retryPolicy(retryPolicy)
                .build();

        log.info("开始连接zk...");
        this.client.start(); // 启动客户端，建立连接
        log.info("zk连接成功");
    }

    /**
     * 创建持久化节点（如果节点不存在）
     * @param path 节点路径
     */
    @SneakyThrows
    public void createPersistentNode(String path){
        if (StrUtil.isBlank(path)){
            throw new IllegalArgumentException("path为空");
        }

        // 检查节点是否已存在
        if (client.checkExists().forPath(path) != null){
            log.info("该节点已存在：{}",path);
            return;
        }

        log.info("创建持久化节点：{}",path);
        // 创建持久化节点，必要时自动创建父节点
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    /**
     * 获取指定路径下的所有子节点
     * @param path 节点路径
     * @return 子节点列表
     */
    @SneakyThrows
    public List<String> getChildrenNode(String path){
        if (StrUtil.isBlank(path)){
            throw new IllegalArgumentException("path为空");
        }

        // 获取并返回子节点列表
        return client.getChildren().forPath(path);
    }

}
