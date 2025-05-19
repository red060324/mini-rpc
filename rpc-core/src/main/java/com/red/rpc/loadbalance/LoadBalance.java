package com.red.rpc.loadbalance;

import java.util.List;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public interface LoadBalance {
    public String select(List<String> list);
}
