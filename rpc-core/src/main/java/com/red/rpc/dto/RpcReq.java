package com.red.rpc.dto;

import cn.hutool.core.util.StrUtil;
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
public class RpcReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 相当于把方法的调用过程抽象到RpcReq里
     */
    private String reqId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    // UserService -> UserServiceImpl1.getUser()
    //             -> UserServiceImpl2.getUser()
    //区分实现的不同版本
    private String version;
    // UserService -> CommonUserServiceImpl.getUser()
    //             -> AdminUserServiceImpl.getUser()
    //区分实现的不同类型
    private String group;

    public String rpcServiceName(){
        return getInterfaceName() + StrUtil.blankToDefault(getVersion(),StrUtil.EMPTY) + StrUtil.blankToDefault(getGroup(),StrUtil.EMPTY);
    }
}
