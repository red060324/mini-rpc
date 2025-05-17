package com.red.rpc.transmission.socket.server;

import com.red.rpc.dto.RpcReq;
import com.red.rpc.dto.RpcResp;
import com.red.rpc.transmission.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author red
 * @date 2025/5/17
 * @description
 */
@Slf4j
public class SocketRpcServer implements RpcServer {

    private final int port;

    public SocketRpcServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null){
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                RpcReq rpcReq = (RpcReq) inputStream.readObject();
                System.out.println(rpcReq);

                //假装调用了rpcReq中的接口实现类的方法 得到数据
                String data = "123123";


                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                RpcResp<String> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
                outputStream.writeObject(rpcResp);
                outputStream.flush();

            }
        } catch (Exception e) {
            log.error("服务端异常",e);
        }
    }
}
