package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        //客户端请求与服务器连接
        try(Socket socket = new Socket( "localhost", 9999)) {
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(socket.getOutputStream());
            //获取Socket的输入流，用来接收从服务端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            boolean flag = true;
//            while (flag) {
//                String echo = buf.readLine();
//                System.out.println(echo);
//                System.out.print("客户端，输入信息：");
//                //获取键盘输入
//                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
//                String str = input.readLine();
//                //发送数据到服务端
//                out.println(str);
//                if ("bye".equals(str)) {
//                    flag = false;
//                }
//            }

            boolean flag = true;
            while (flag) {
                //获取键盘输入
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                Thread readThread = new Thread() {
                    public void run() {
                        while (true) {
                            String msg = null;
                            try {
                                msg = buf.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(msg);
                        }
                    }
                };

                Thread writeThread = new Thread() {
                    public void run() {
                        while (true) {
                            try {
                                out.print(input.readLine());
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                readThread.start();
                writeThread.start();
            }
        }
    }

}
