package com.red.client;

import com.red.api.User;
import com.red.api.UserService;
import com.red.client.utils.ProxyUtils;
import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcClient;
import com.red.rpc.transmission.netty.client.NettyRpcClient;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public class Main {
    public static void main(String[] args) {
        //获取某个接口的代理实现
        UserService userService = ProxyUtils.getProxy(UserService.class);
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        while (true){
            System.out.println("请输入请求数量：");
            int n = scanner.nextInt();
            System.out.println("请输入用户id：");
            Long id = scanner.nextLong();

            for (int i = 0; i < n; i++) {
                executorService.execute(() -> {
                    try {
                        User user1 = userService.getUser(id);
                        System.out.println(user1);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }



    }



}
