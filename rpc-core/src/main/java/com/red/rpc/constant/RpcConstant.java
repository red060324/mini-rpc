package com.red.rpc.constant;

/**
 * @author red
 * @date 2025/5/19
 * @description
 */
public class RpcConstant {
    public static final int SERVER_PORT = 9999;


    public static final String ZK_IP ="192.168.214.201";
    public static final int ZK_PORT =2181;
    public static final String ZK_RPC_ROOT_PATH ="/red-rpc";

    public static final String NETTY_RPC_KEY ="RpcResp";
    public static final byte[] RPC_MAGIC_CODE = new byte[]{(byte) 'r',(byte) 'r',(byte) 'p',(byte) 'c'};
    public static final int REQ_HEAD_LEN = 16;
    public static final int REQ_MAX_LEN = 1024 * 1024;

}
