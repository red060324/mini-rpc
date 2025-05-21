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
@Getter
@AllArgsConstructor
public enum SerializeType {

    KRYO((byte) 1, "Kryo序列化"),
    ;

    private final byte code;
    private final String desc;

    public static SerializeType from(byte code){
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到对应的code: " + code));
    }
}
