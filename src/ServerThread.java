import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    OutputStream output;//输出流
    InputStream input;//输入流
    Socket socket;//定义一个socket接收对象的属性

    //线程创建方法
    @Override
    public void run() {
        try {
            System.out.println("已启动一个线程来处理~");

            //为输入输出流赋值
            output=socket.getOutputStream();
            input=socket.getInputStream();
            //开始通信
            //传送信息给客户机
            String outS="Hello,welcome to my ServerSocket!\r\n";
            out(outS);

            //发送信息给服务器
            ReadString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //定义一个建立服务器的方法
    private void setUpServer(int port) throws IOException {
        //将输入的端口设置为服务器
        ServerSocket server=new ServerSocket(port);
        //输出当前服务器的端口号
        System.out.println("服务器创建成功，端口号："+server.getLocalPort());
        while(true) {
            ServerThread ts=new ServerThread();
            //为对象的socket属性赋值。
            ts.socket=server.accept();
            //启动线程
            ts.start();
        }
    }

    //定义一个输出信息到客户机的方法
    private void out(String outS) throws IOException {
        //将字符串转化为byte数组
        byte[] dataout=outS.getBytes();
        //调用write()将信息发送客户机
        output.write(dataout);
        //强制输出到命令行的界面中
        output.flush();
    }

    //定义一个传送字符串给服务器的方法
    public void ReadString() throws IOException {
        String inputS="";
        while(!inputS.equals("bye")) {
            //读取第一个字符
            int AsciiNumber=input.read();
            while(AsciiNumber!=13) {
                //将ascii码转化为相应的char型字符
                inputS+=(char)AsciiNumber;
                //接收下一个字符
                AsciiNumber=input.read();
            }
            System.out.println(inputS);
        }
        //关闭连接
        output.close();
    }

    //主函数入口
    public static void main(String[] args) throws IOException {
        //创建一个通信类的对象
        ServerThread server=new ServerThread();
        server.setUpServer(9014);
    }

}