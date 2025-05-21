package com.red.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
@Getter
@AllArgsConstructor
@ToString
public enum MsgType {
    /**
     * 心跳请求
     */
    HEARTBEAT_REQ((byte) 1, "心跳请求"),
    /**
     * 心跳响应
     */
    HEARTBEAT_RESP((byte) 2, "心跳响应"),

    /**
     * rpc请求
     */
    RPC_REQ((byte) 3, "RPC请求"),

    /**
     * rpc响应
     */
    RPC_RESP((byte) 4, "RPC响应");



    private final byte code;
    private final String desc;

    public boolean isHeartbeat() {
        return this == HEARTBEAT_REQ || this == HEARTBEAT_RESP;
    }
}
