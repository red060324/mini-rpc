package com.red.rpc.util;

import com.red.rpc.factory.SingletonFactory;
import com.red.rpc.registry.impl.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author red
 * @date 2025/5/18
 * @description
 */
@Slf4j
public class ShutdownHookUtils {
    public static void clearAll(){
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            log.info("系统结束运行，清理资源");
            ZkServiceRegistry serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
            serviceRegistry.clearAll();
            ThreadPoolUtils.shutdownAll();
        }));
    }
}
