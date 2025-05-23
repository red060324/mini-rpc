package com.red.api;

import com.red.rpc.annotation.Retry;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public interface UserService {
    @Retry
    User getUser(Long id);
}
