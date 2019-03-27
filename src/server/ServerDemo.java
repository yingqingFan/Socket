package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerDemo {

    public static void main(String[] args) throws IOException {
        //创建一个通信类的对象
        ServerSocket server = new ServerSocket(9999);
        //输出当前服务器的端口号
        System.out.println("服务器创建成功，端口号：" + server.getLocalPort());
        //容纳三个线程的线程池
        Executor pool = Executors.newFixedThreadPool(100);
        boolean flag = true;
        while (flag) {
            //new一个线程与客户端交互,server.accept()等待连接,pool执行线程
            pool.execute(new ServerThread(server.accept()));
        }
    }

}
