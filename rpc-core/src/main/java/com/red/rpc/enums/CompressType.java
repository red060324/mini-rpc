package com.red.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

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

    public static CompressType from(byte code){
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到对应的code: " + code));
    }
}
