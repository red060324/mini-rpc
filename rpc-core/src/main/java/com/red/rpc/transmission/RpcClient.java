package com.red.rpc.transmission;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;

import java.util.concurrent.Future;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
public interface RpcClient {
    Future<RpcResp<?>> sendReq(RpcReq req);
}
