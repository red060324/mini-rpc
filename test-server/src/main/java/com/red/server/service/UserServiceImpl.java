package com.red.server.service;

import cn.hutool.core.util.IdUtil;
import com.red.api.User;
import com.red.api.UserService;
import com.red.rpc.annotation.Limit;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class UserServiceImpl implements UserService {
    @Limit(permitsPerSecond = 5, timeout = 0)
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(++id)
                .name("张三")
                .build();
    }
}
