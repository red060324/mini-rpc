package com.red.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
@ToString
@Getter
@AllArgsConstructor
public enum SerializeType {

    CUSTOM((byte) 0, "custom"),
    KRYO((byte) 1, "kryo"),
    HESSIAN((byte) 2, "hessian"),
    PROTOSTUFF((byte) 3, "protostuff"),
    ;

    private final byte code;
    private final String desc;

    public static SerializeType from(byte code){
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElse(CUSTOM);
    }

    public static SerializeType from(String desc){
        return Arrays.stream(values())
                .filter(o -> Objects.equals(desc, o.desc))
                .findFirst()
                .orElse(CUSTOM);
    }
}
