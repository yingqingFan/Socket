package com.ls.socket.server;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ServerTest {
    private static Logger log = Logger.getLogger(com.ls.socket.server.ServerTest.class);
    public static void main(String[] args) throws IOException {
        if(ArrayUtils.isEmpty(args)){
            log.error("必须指定数据存储位置(文件名默认为：messageInfo.txt)");
            System.exit(0);
        }
        String path = args[0];
        SocketServer.run(9999, path);
    }
}
