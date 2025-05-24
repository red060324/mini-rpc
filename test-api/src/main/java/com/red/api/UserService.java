package com.red.api;

import com.red.rpc.annotation.Breaker;
import com.red.rpc.annotation.Limit;
import com.red.rpc.annotation.Retry;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public interface UserService {
//    @Retry
    @Breaker(widowTime = 30000L)
    User getUser(Long id);
}
