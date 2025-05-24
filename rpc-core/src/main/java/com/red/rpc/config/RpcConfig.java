package com.red.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author red
 * @date 2025/5/24
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcConfig {
    private String serializer = "kryo";

}
