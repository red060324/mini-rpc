package com.red.rpc.transmission.socket.server;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.handler.RpcReqHandler;
import com.red.rpc.provider.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author red
 * @date 2025/5/18
 * @description
 */
@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable{
    private final Socket socket;
    private final RpcReqHandler rpcReqHandler;

    @SneakyThrows
    @Override
    public void run() {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        RpcReq rpcReq = (RpcReq) inputStream.readObject();
        System.out.println(rpcReq);

        //调用了rpcReq中的接口实现类的方法 得到数据
        Object data = rpcReqHandler.invoke(rpcReq);

        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        RpcResp<?> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
        outputStream.writeObject(rpcResp);
        outputStream.flush();

    }
}
