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
public enum VersionType {
    VERSION1((byte) 1,"版本1");
    private final byte code;
    private final String desc;
}
