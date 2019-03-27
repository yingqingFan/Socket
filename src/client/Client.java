package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    public static void main(String[] args) throws IOException {
        //客户端请求与服务器连接
        Socket socket = new Socket( "localhost", 9999);
        //获取Socket的输出流，用来发送数据到服务端
        PrintStream out = new PrintStream(socket.getOutputStream());
        //获取Socket的输入流，用来接收从服务端发送过来的数据
        BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String echo = buf.readLine();
        System.out.println(echo);

        while(true) {
            System.out.print("客户端，输入信息：");
            //获取键盘输入
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String str = input.readLine();
            //发送数据到服务端
            out.println(str);
            BufferedReader buf1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String echo1 = buf1.readLine();
            System.out.println(echo1);
        }

    }

}
