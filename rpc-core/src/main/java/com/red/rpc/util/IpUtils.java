package com.red.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * IP 工具类，提供 IP 与端口相关的转换方法
 * 包括 InetSocketAddress 与字符串之间的互转
 * @author red
 * @date 2025/5/19
 */
public class IpUtils {
    /**
     * 将 InetSocketAddress 转换为 "ip:port" 字符串
     *
     * @param address InetSocketAddress 对象
     * @return 形如 "ip:port" 的字符串
     */
    public static String toIpPort(InetSocketAddress address) {
        if (Objects.isNull(address)){
            throw new IllegalArgumentException("address is null");
        }

        // 获取主机名或 IP
        String host = address.getHostString();
        // 如果是 localhost，转换为 127.0.0.1
        if (Objects.equals(host,"localhost")) {
            host = "127.0.0.1";
        }

        // 返回 ip:port 格式字符串
        return host + StrUtil.COLON + address.getPort();
    }

    /**
     * 将 "ip:port" 字符串转换为 InetSocketAddress 对象
     *
     * @param address 形如 "ip:port" 的字符串
     * @return InetSocketAddress 对象
     */
    public static InetSocketAddress toInetSocketAddress(String address) {
        if (StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("address is blank");
        }

        // 按冒号分割字符串
        String[] split = address.split(StrUtil.COLON);
        if (split.length != 2) {
            throw new IllegalArgumentException("address format error");
        }

        String host = split[0];
        int port = Integer.parseInt(split[1]);
        // 构造 InetSocketAddress 对象
        return new InetSocketAddress(host, port);
    }
}

