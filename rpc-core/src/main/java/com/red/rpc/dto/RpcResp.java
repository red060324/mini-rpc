package com.red.rpc.dto;

import com.red.rpc.enums.RpcRespStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author red
 * @date 2025/5/16
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResp<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private Integer code;
    private String msg;
    private T data;

    public static <T> RpcResp<T> success(String reqId, T data){
        RpcResp<T> resp = new RpcResp<T>();
        resp.setReqId(reqId);
        resp.setCode(RpcRespStatus.SUCCESS.getCode());
        resp.setData(data);
        return resp;
    }

    public static <T> RpcResp<T> fail(String reqId, String msg){
        RpcResp<T> resp = new RpcResp<T>();
        resp.setReqId(reqId);
        resp.setCode(RpcRespStatus.FAIL.getCode());
        resp.setMsg(msg);
        return resp;
    }
}
