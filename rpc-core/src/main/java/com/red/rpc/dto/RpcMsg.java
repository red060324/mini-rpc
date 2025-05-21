package com.red.rpc.dto;

import com.red.rpc.enums.CompressType;
import com.red.rpc.enums.MsgType;
import com.red.rpc.enums.SerializeType;
import com.red.rpc.enums.VersionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer reqId;
    private VersionType version;
    private MsgType msgType;
    private SerializeType serializeType;
    private CompressType compressType;
    private Object data;

}
