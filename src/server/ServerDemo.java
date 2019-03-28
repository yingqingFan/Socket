package server;

import Entity.MessageInfo;
import client.ClientDemo;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerDemo {

    Map<String, Socket> socketMap = new HashMap<>();
    Map<String, MessageInfo> messageHistoryMap = new HashMap<>();
    Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        ServerDemo serverDemo = new ServerDemo();
        //创建一个通信类的对象
        ServerSocket server = new ServerSocket(9999);
        //输出当前服务器的端口号
        System.out.println("服务器创建成功，端口号：" + server.getLocalPort());
        //容纳三个线程的线程池
        Executor pool = Executors.newFixedThreadPool(100);
        boolean flag = true;
        while (flag) {
            Socket socket = server.accept();
            String socketId = null;
            while(StringUtils.isEmpty(socketId)||serverDemo.socketMap.get(socketId)!= null){
                socketId =((int) (Math.random()*100))+"";
            }
            serverDemo.socketMap.put(socketId,socket);
            //new一个线程与客户端交互,server.accept()等待连接,pool执行线程
//            pool.execute(serverDemo.new ServerThread(socket, socketId));
            serverDemo.new ServerThread(socket, socketId+"").start();
        }
    }

    class ServerThread extends Thread {
        PrintWriter writer;//输出流
        BufferedReader bufferedReader;//输入流
        Socket socket;
        String id;

        public ServerThread(Socket socket, String id) {
            this.socket = socket;
            this.id = id;
        }


        @Override
        public void run() {
            System.out.println("thread id:"+Thread.currentThread().getId());
            //客户端连接后获取socket输出输入流
            try {
                writer = new PrintWriter(socket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //连接成功提示
                String successMessage =  "当前client" + id + " 连接成功";
                out(successMessage,id);

                //告诉其他客户端当前客户端上线
                String outS = "client" + id + " 已上线";
                outOthers(outS);

                //读取客户端信息并转发
                readAndSend();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //输出信息到指定客户端
        private void out(String outS, String socketId) {
            try {
                Socket socket1 = socketMap.get(socketId);
                if(socket1!=null) {
                    PrintWriter printWriter1 = new PrintWriter(socket1.getOutputStream());
                    //将信息发送客户机
                    printWriter1.println(outS);
                    //强制输出到命令行的界面中
                    printWriter1.flush();
                }else{
                    PrintWriter printWriter1 = new PrintWriter(socket.getOutputStream());
                    //提示客户端指定客户端不存在
                    printWriter1.println("指定client"+socketId+" 不存在");
                    //强制输出到命令行的界面中
                    printWriter1.flush();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        //输出信息到其他所有客户端
        public void outOthers(String outS) {
            Iterator<String> idIterator = socketMap.keySet().iterator();
            while(idIterator.hasNext()){
                String sId = idIterator.next();
                if(!sId.equals(id)) {
                    out(outS, sId);
                }
            }
        }

        public void readAndSend() {
            String line = null;
            try {
                while (true) {
                    if (((line = bufferedReader.readLine()) != null)) {
                        System.out.println("内容 : " + line);
                        MessageInfo messageInfo = gson.fromJson(line, MessageInfo.class);
                        //如果客户端是发送消息
                        if(messageInfo.getAction().equals(ClientDemo.ACTIONS[0])) {
                            messageInfo.setClientId(id);
                            //将messageInfo存入内存
                            messageHistoryMap.put("1", messageInfo);
                            String socketIdTo = messageInfo.getFriendClientId();
                            String message = messageInfo.getMessageContent();
                            //发送信息给目标客户端
                            out("client: " + id + " : " + message, socketIdTo);
                        }
                        //如果客户端是查看聊天历史记录，返回历史记录给客户端
                        else if(messageInfo.getAction().equals(ClientDemo.ACTIONS[1])){
                            Iterator<String> messageIterator = messageHistoryMap.keySet().iterator();
                            while(messageIterator.hasNext()){
                                String messageId = messageIterator.next();
                                MessageInfo sendHistory = messageHistoryMap.get(messageId);
                                if((sendHistory.getClientId().equals(id) && sendHistory.getFriendClientId().equals(messageInfo.getFriendClientId()))
                                        || (sendHistory.getClientId().equals(messageInfo.getFriendClientId()) && sendHistory.getFriendClientId().equals(id)) ) {
                                    String messageStr ="clientId " + sendHistory.getClientId() + ":" + sendHistory.getMessageContent();
                                    out(messageStr, id);
                                }
                            }
                        }
                        //如果客户端是查看在线用户，返回在线用户给客户端
                        else if(messageInfo.getAction().equals(ClientDemo.ACTIONS[2])){
                            Iterator<String> idIterator = socketMap.keySet().iterator();
                            while(idIterator.hasNext()){
                                String sId = idIterator.next();
                                out(sId,id);
                            }
                        }

                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                socketMap.remove(id);
                System.out.println("移除 client"+id);
                String outS = "client" + id + " 已下线";
                outOthers(outS);
            }
        }
    }
}
