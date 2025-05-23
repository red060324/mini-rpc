package com.red.rpc.transmission.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author red
 * @date 2025/5/23
 * @description
 */
public class ChannelPool {
    private final Map<String, Channel> pool = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress address, Supplier<Channel> supplier) {
        String addrString = address.toString();
        Channel channel = pool.get(addrString);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        // 如果没有可用的 channel，则创建一个新的
        Channel newChannel = supplier.get();
        pool.put(addrString, newChannel);
        return newChannel;
    }
}
