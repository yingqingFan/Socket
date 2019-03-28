package client;

import Entity.MessageInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientDemo2 {
    public static final String[] ACTIONS = new String[]{"send message", "view history", "view online clients"};
    public static String ACTION = null;
    public static String FRIEND_ClIENTID = null;
    public static void main(String[] args) throws IOException {
        ClientDemo2 clientDemo2 = new ClientDemo2();
        //客户端请求与服务器连接
        Socket socket = new Socket( "localhost", 9999);
        //获取Socket的输出流，用来发送数据到服务端
        PrintStream out = new PrintStream(socket.getOutputStream());

        //接收信息
        clientDemo2.new ClientThread(socket).start();
        while(true) {
            MessageInfo messageInfo = clientDemo2.initMessageInfo();
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
                    String echo1 = buf1.readLine();
                    System.out.println(echo1);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                    completeMessageInfoByFriendId(FRIEND_ClIENTID,messageInfo);
                }else{
                    System.out.print("请输入好友clientId:");
                    String friendClientId = scanner.next();
                    completeMessageInfoByFriendId(friendClientId,messageInfo);
                }
            }else if(ACTION.equals(ACTIONS[1])){
                System.out.print("你想查询和谁之间的历史记录，请输入对方clientId:(按#键加Enter键退出历史查询)");
                String friendClientId = scanner.next();
                completeMessageInfoByFriendId(friendClientId,messageInfo);
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
                    FRIEND_ClIENTID = friendClientId;
                    messageInfo.setFriendClientId(FRIEND_ClIENTID);
                    System.out.println("To client:"+ FRIEND_ClIENTID + "(按Enter键发送消息,按#键和Enter退出聊天):");
                    String messageContent = scanner.next();
                    if(messageContent.equals("#")){
                        return exit();
                    }
                    messageInfo.setMessageContent(messageContent);
                    break;
                case "1":
                    messageInfo.setAction(ACTIONS[1]);
                    System.out.print("你想查询和谁之间的历史记录，请输入对方clientId:");
                    messageInfo.setFriendClientId(scanner.next());
                    break;
                case "2":
                    messageInfo.setAction(ACTIONS[2]);
                    break;
                default:
                    System.out.println("choose error!");
                    initMessageInfo();
                    break;
            }
        }
        return messageInfo;
    }


    public MessageInfo exit()throws IOException{
        ACTION = null;
        return initMessageInfo();
    }

    public void completeMessageInfoByFriendId(String friendId, MessageInfo messageInfo) throws IOException{
        Scanner scanner = new Scanner(System.in);
        FRIEND_ClIENTID = friendId;
        messageInfo.setFriendClientId(FRIEND_ClIENTID);
        if(messageInfo.getAction().equals(ACTIONS[0])) {
            System.out.println("To client:" + FRIEND_ClIENTID + "(按Enter键发送消息,按#键和Enter退出聊天):");
        }
        String messageContent = scanner.next();
        if(messageContent.equals("#")){
            messageInfo = exit();
        }
        messageInfo.setMessageContent(messageContent);
    }

}
