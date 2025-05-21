package com.red.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
@ToString
@AllArgsConstructor
@Getter
public enum CompressType {
    /**
     * gzip压缩
     */
    GZIP((byte) 1, "gzip压缩"),

    ;

    private final byte code;
    private final String desc;
}
