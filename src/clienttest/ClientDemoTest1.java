package clienttest;

import Entity.MessageInfo;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ClientDemoTest1 {
    public static final String[] ACTIONS = new String[]{"send message", "view history", "view online clients", "bind"};
    public static String ACTION = null;
    public static String FRIEND_ClIENTID = null;
    Scanner scanner = new Scanner(System.in);
    Gson gson = new Gson();
    PrintStream out = null;
    String clientId = null;
    public static void main(String[] args) throws IOException {
        String clientId = null;
        if(ArrayUtils.isEmpty(args)){
            System.out.println("必须指定客户端Id");
            return;
        }else{
            clientId = args[0];
            if(StringUtils.isEmpty(clientId)){
                System.out.println("必须指定客户端Id");
                return;
            }
        }
        ClientDemoTest1 clientDemoTest1 = new ClientDemoTest1();
        clientDemoTest1.clientId = clientId;
        Socket socket = clientDemoTest1.initClient();
        //检测心跳重连
        clientDemoTest1.new HeatBeat(socket).start();
        if(socket!=null){
            //接收消息
            clientDemoTest1.receiveMessage(socket);
            //发送消息
            clientDemoTest1.sendMessage();
        }
    }

    public Socket initClient(){
        Socket socket = null;
        try {
            //客户端请求与服务器连接
            socket = new Socket( "localhost", 9999);
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            this.out = printStream;
            //绑定客户端信息
            bindInfoWithServer(clientId, out);
        }catch (IOException e){
            System.out.println("服务器未连接");
            socket = null;
        }
        return socket;
    }

    //绑定clientId
    public void bindInfoWithServer(String clientId, PrintStream out){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setAction(ACTIONS[3]);
        //将clientId发送到服务端
        messageInfo.setClientId(clientId);
        out.println(gson.toJson(messageInfo));
    }

    public void receiveMessage(Socket socket){
        //开启线程接收信息
        new ClientThread(socket).start();
    }

    public void sendMessage(){
        //循环接收指令发送消息
        while(true) {
            MessageInfo messageInfo = initMessageInfo();
            if(messageInfo == null){
                continue;
            }
            messageInfo.setDate(new Date());
            String str = gson.toJson(messageInfo);
            //发送数据到服务端
            try {
                out.println(str);
            }catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
        }
    }


    public MessageInfo initMessageInfo(){
        MessageInfo messageInfo = new MessageInfo();
        Scanner scanner = new Scanner(System.in);
        if(ACTION != null && ACTION != ACTIONS[2]){
            messageInfo.setAction(ACTION);
            if(ACTION.equals(ACTIONS[0])){
                if(FRIEND_ClIENTID != null){
                    messageInfo = completeSendMessageInfoById(FRIEND_ClIENTID,messageInfo);
                }else{
                    System.out.print("请输入好友clientId:");
                    String friendClientId = scanner.next();
                    messageInfo = completeSendMessageInfoById(friendClientId,messageInfo);
                }
            }else if(ACTION.equals(ACTIONS[1])){
                messageInfo = completeHistoryMessageInfo(messageInfo);
            }
        }else {
            System.out.println("请选择操作序号：0." + ACTIONS[0] + " 1." + ACTIONS[1] + " 2." + ACTIONS[2]);
            String orderNumber = scanner.next();
            switch (orderNumber) {
                case "0":
                    ACTION = ACTIONS[0];
                    messageInfo.setAction(ACTION);
                    System.out.print("请输入好友clientId:");
                    String friendClientId = scanner.next();
                    messageInfo = completeSendMessageInfoById(friendClientId, messageInfo);
                    break;
                case "1":
                    ACTION = ACTIONS[1];
                    messageInfo.setAction(ACTION);
                    messageInfo = completeHistoryMessageInfo(messageInfo);
                    break;
                case "2":
                    messageInfo.setAction(ACTIONS[2]);
                    break;
                default:
                    System.out.println("没有该选项，请重新选择!");
                    messageInfo = null;
                    break;
            }
        }
        return messageInfo;
    }

    public MessageInfo completeSendMessageInfoById(String friendId, MessageInfo messageInfo){
        FRIEND_ClIENTID = friendId;
        messageInfo.setFriendClientId(FRIEND_ClIENTID);
        System.out.println("To client" + FRIEND_ClIENTID + "(按Enter键发送消息,按#键和Enter退出聊天):");
        String messageContent = scanner.next();
        if (messageContent.equals("#")) {
            ACTION = null;
            return null;
        }
        messageInfo.setMessageContent(messageContent);
        return messageInfo;
    }

    public MessageInfo completeHistoryMessageInfo(MessageInfo messageInfo){
        System.out.println("你想查询和谁之间的历史记录，请输入对方clientId(按#键加Enter键退出历史查询)：");
        String friendClientId = scanner.next();
        if (friendClientId.equals("#")) {
            ACTION = null;
            return null;
        }
        messageInfo.setFriendClientId(friendClientId);
        return messageInfo;
    }

    class ClientThread extends Thread{
        private Socket socket;
        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while(true) {
                    BufferedReader buf1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line = null;
                    while (((line = buf1.readLine()) != null)) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("无法连接服务器");
                return;
            }
        }
    }

    class HeatBeat extends Thread{
        private Socket socket;
        public HeatBeat(Socket socket) {
            this.socket = socket;
        }

        public Boolean isServerClose(Socket socket){
            try{
                socket.sendUrgentData(0xFF);
                return false;
            }catch(Exception se){
                return true;
            }
        }

        @Override
        public void run() {
            try {
                while(true) {
                    Thread.sleep(5000);
                    if(socket == null || (socket != null && isServerClose(socket))){
                        System.out.println("尝试重新连接...");
                        socket = initClient();
                        if(socket!=null){
                            //接收消息
                            receiveMessage(socket);
                            //发送消息
                            sendMessage();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
