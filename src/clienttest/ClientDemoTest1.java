package clienttest;

import Entity.MessageInfo;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ClientDemoTest1 {
    public static final String[] ACTIONS = new String[]{"send message", "view history", "view online clients", "bind"};
    public static String ACTION = null;
    public static String FRIEND_ClIENTID = null;
    Scanner scanner = new Scanner(System.in);
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
        //客户端请求与服务器连接
        Socket socket;
        try {
            socket = new Socket( "localhost", 9999);
        }catch (ConnectException e){
//            e.printStackTrace();
            System.out.println("无法连接服务器");
            return;
        }
        //获取Socket的输出流，用来发送数据到服务端
        PrintStream out = new PrintStream(socket.getOutputStream());
        //绑定客户端信息
        clientDemoTest1.bindInfoWithServer(clientId, out);
        //接收信息
        clientDemoTest1.new ClientThread(socket).start();
        //循环接收指令发送消息
        while(true) {
            MessageInfo messageInfo = clientDemoTest1.initMessageInfo();
            if(messageInfo == null){
                continue;
            }
            messageInfo.setDate(new Date());
            Gson gson = new Gson();
            String str = gson.toJson(messageInfo);
            //发送数据到服务端
            out.println(str);
        }
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
//                e.printStackTrace();
                System.out.println("无法连接服务器");
            }
        }
    }

    public MessageInfo initMessageInfo() throws IOException{
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
            System.out.println("请选择你要做的操作序号：0." + ACTIONS[0] + " 1." + ACTIONS[1] + " 2." + ACTIONS[2]);
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

    public MessageInfo completeSendMessageInfoById(String friendId, MessageInfo messageInfo) throws IOException{
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

    public MessageInfo completeHistoryMessageInfo(MessageInfo messageInfo) throws IOException{
        System.out.println("你想查询和谁之间的历史记录，请输入对方clientId(按#键加Enter键退出历史查询)：");
        String friendClientId = scanner.next();
        if (friendClientId.equals("#")) {
            ACTION = null;
            return null;
        }
        messageInfo.setFriendClientId(friendClientId);
        return messageInfo;
    }

    //绑定clientId
    public void bindInfoWithServer(String clientId, PrintStream out){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setAction(ACTIONS[3]);
        //将clientId发送到服务端
        messageInfo.setMessageContent(clientId);
        out.println();
    }
}
