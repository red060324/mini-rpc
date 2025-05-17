package com.red.rpc.transmission.socket.client;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class SocketRpcClient implements RpcClient {
    private final String host;
    private final int port;

    public SocketRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        try (Socket socket = new Socket(host,port)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(req);
            outputStream.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object o = inputStream.readObject();
            return (RpcResp<?>) o;
        } catch (Exception e) {
            log.error("发送rpc请求失败",e);
        }
        return null;
    }
}
