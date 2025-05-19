package com.red.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.red.rpc.constant.RpcConstant;
import com.red.rpc.exception.RpcException;
import com.red.rpc.util.IpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zookeeper 客户端工具类，封装了 CuratorFramework 的基本操作
 * 用于连接 Zookeeper、创建节点、获取子节点等功能
 * 
 * @author red
 * @date 2025/5/19
 */
@Slf4j
public class ZkClient {
    // CuratorFramework 客户端实例
    private final CuratorFramework client;
    // 重试之间等待的初始时间（毫秒）
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;
    // key为/red-rpc/rpcServiceName value为childrenNode[ip:port]
    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();
    // /red-rpc/rpcServiceName/ip:port
    private static final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

    /**
     * 默认构造方法，使用常量中的 zk 地址和端口
     */
    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    /**
     * 构造方法，指定 zk 地址和端口
     * 
     * @param hostName zk 服务端地址
     * @param port     zk 服务端端口
     */
    public ZkClient(String hostName, int port) {
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
     * 
     * @param path 节点路径
     */
    @SneakyThrows
    public void createPersistentNode(String path) {

        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path为空");
        }

        if (SERVICE_ADDRESS_SET.contains(path)) {
            log.info("该节点已存在：{}", path);
            return;
        }

        // 检查节点是否已存在
        if (client.checkExists().forPath(path) != null) {
            SERVICE_ADDRESS_SET.add(path);
            log.info("该节点已存在：{}", path);
            return;
        }

        log.info("创建持久化节点：{}", path);
        // 创建持久化节点，必要时自动创建父节点
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);

        SERVICE_ADDRESS_SET.add(path);
    }

    /**
     * 获取指定路径下的所有子节点
     * 
     * @param path 节点路径
     * @return 子节点列表
     */
    @SneakyThrows
    public List<String> getChildrenNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path为空");
        }

        if (SERVICE_ADDRESS_CACHE.containsKey(path)) {
            log.info("缓存中已存在该节点：{}", path);
            return SERVICE_ADDRESS_CACHE.get(path);
        }

        // 获取并返回子节点列表
        List<String> children = client.getChildren().forPath(path);
        SERVICE_ADDRESS_CACHE.put(path, children);
        log.info("获取子节点成功：{}，子节点：{}", path, children);
        watchChildrenNode(path);
        return children;
    }

    /**
     * 删除当前服务端在 Zookeeper 上注册的所有与指定 address 相关的节点。
     *
     * 调用时机：
     * - 通常在服务端优雅下线、重启或注销服务时调用，
     *   由上层 ServiceProvider 或 RpcServer 控制，确保注册中心无脏数据。
     */
    public void clearAll(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address为空");
        }

        SERVICE_ADDRESS_SET.forEach(path -> {
            if (path.endsWith(IpUtils.toIpPort(address))) {
                log.debug("zk节点删除成功：{}", path);
                try {
                    client.delete().forPath(path);
                } catch (Exception e) {
                    log.error("zk节点删除失败：{}", path);
                    throw new RpcException("zk节点删除失败", e);
                }
            }
        });

    }
    /**
     * clearAll 和 watchChildrenNode 的作用区别：
     *
     * 1. clearAll
     *    - 作用：用于服务端主动注销自身在 Zookeeper 注册中心的所有服务节点（如服务下线、重启时）。
     *    - 影响范围：只影响本服务端注册的节点，不影响其他服务实例。
     *    - 典型场景：服务端优雅下线、重启、注销服务时调用，确保注册中心无本机残留节点。
     *
     * 2. watchChildrenNode
     *    - 作用：用于客户端（或服务消费者）监听某服务在 Zookeeper 下的所有子节点变化（如服务实例上线/下线）。
     *    - 影响范围：只负责感知服务列表变化，自动同步本地缓存，便于客户端动态发现可用服务实例。
     *    - 典型场景：客户端获取服务列表时自动注册监听，保证服务发现的实时性和一致性。
     *
     * 总结：
     * - clearAll 是服务端主动删除自身节点，属于服务注销操作。
     * - watchChildrenNode 是客户端被动监听服务节点变化，属于服务发现机制。
     */

    /**
     * 监听指定 path 下子节点的变化（如服务实例上线/下线）。
     *
     * 调用时机：
     * - 在 getChildrenNode 方法中首次获取某服务的子节点时自动调用，
     *   用于动态感知服务列表变化，自动同步本地缓存。
     */
    @SneakyThrows
    private void watchChildrenNode(String path) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);

        // 给某个节点注册子节点监听器
        PathChildrenCacheListener pathChildrenCacheListener = (curClient, event) -> {
            List<String> children = curClient.getChildren().forPath(path);
            SERVICE_ADDRESS_CACHE.put(path, children);
            log.info("监听到子节点变化，更新本地缓存：{} -> {}", path, children);

        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        // 启动pathChildrenCache
        pathChildrenCache.start();
    }

}
