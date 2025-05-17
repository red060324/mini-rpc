package com.red.server.service;

import cn.hutool.core.util.IdUtil;
import com.red.api.User;
import com.red.api.UserService;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(id)
                .name(IdUtil.fastSimpleUUID())
                .build();
    }
}
