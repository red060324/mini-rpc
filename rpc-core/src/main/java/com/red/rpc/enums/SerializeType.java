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
@Getter
@AllArgsConstructor
public enum SerializeType {

    KRYO((byte) 1, "Kryo序列化"),
    ;

    private final byte code;
    private final String desc;
}
