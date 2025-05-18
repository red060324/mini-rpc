package com.red.client;

import com.red.api.User;
import com.red.api.UserService;
import com.red.client.utils.ProxyUtils;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    public static void main(String[] args) {
        //获取某个接口的代理实现
        UserService userService = ProxyUtils.getProxy(UserService.class);
        //像调用本地方法一样调用
        User user = userService.getUser(1L);
        System.out.println(user);

    }



}
