package com.red.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public class IpUtils {
    /**
     * 获取本机的 IP 地址
     *
     * @return 本机的 IP 地址
     */
    public static String toIpPort(InetSocketAddress address) {
        if (Objects.isNull(address)){
            throw new IllegalArgumentException("address is null");
        }

        String host = address.getHostString();
        if (Objects.equals(host,"localhost")) {
            host = "127.0.0.1";
        }

        return host + StrUtil.COLON + address.getPort();
    }

    public static InetSocketAddress toInetSocketAddress(String address) {
        if (StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("address is blank");
        }

        String[] split = address.split(StrUtil.COLON);
        if (split.length != 2) {
            throw new IllegalArgumentException("address format error");
        }

        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
