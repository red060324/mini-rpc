package com.red.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.red.rpc.loadbalance.LoadBalance;

import java.util.List;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list) {
        return RandomUtil.randomEle(list);
    }
}
