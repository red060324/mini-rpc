package com.red.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
@Getter
@ToString
@AllArgsConstructor
public enum RpcRespStatus {
    SUCCESS(0,"success"),
    FAIL(9999,"fail");

    private final int code;
    private final String msg;

    public static boolean isSuccessful(Integer code){
        return SUCCESS.getCode() == code;
    }

    public static boolean isFailed(Integer code){
        return !isSuccessful(code);
    }
}
