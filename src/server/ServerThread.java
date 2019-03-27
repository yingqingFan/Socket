package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{
    PrintWriter writer;//输出流
    BufferedReader bufferedReader;//输入流
    Socket socket;

    public ServerThread(Socket socket){
        this.socket = socket;
    }

    public ServerThread(){
    }

    @Override
    public void run() {
        //客户端连接后获取socket输出输入流
        try {
            writer = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //告诉客户端连接成功
            String outS = "connect to server success\r\n";
            out(outS);
            //读取客户端信息
            readClient(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //定义一个输出信息到客户机的方法
    private void out(String outS) throws IOException {
        //将信息发送客户机
        writer.println(outS);
        //强制输出到命令行的界面中
        writer.flush();
    }

    //定义一个传送字符串给服务器的方法
    public void readClient(Socket socket) throws IOException {
        String line = null;
        while((line = bufferedReader.readLine())!= null) {
            System.out.println("内容 : " + line);
//            if (line.equals("bye")){
//                output.close();
//            }
        }
//        System.out.println("read end");
    }

}
