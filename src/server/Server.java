//package server;
//
//
//import Entity.MessageInfo;
//import Entity.UserInfo;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Server {
//
//    private static ServerSocket server;
//
//    public List<Socket> socketList = new ArrayList<>();
//
//    public Map<Integer, Socket> socketMap = new HashMap();
//
//    public Map<Integer, UserInfo> userMap = new HashMap();
//
//
//    public void initServer() {
//        try {
//            // 创建一个ServerSocket
//            server = new ServerSocket(9999);
//            createMessage();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 创建消息
//     */
//    private void createMessage() {
//        try {
//            System.out.println("等待用户连接...");
//            // 使用accept()阻塞等待客户请求
//            Socket socket = server.accept();
//            // 将链接进来的socket保存到集合中
//            socketList.add(socket);
//            System.out.println("用户接入 : " + socket.getPort());
//            // 开启一个子线程来等待另外的socket加入
//            new Thread(new Runnable() {
//                public void run() {
//                    // 再次创建一个socket服务等待其他用户接入
//                    createMessage();
//                }
//            }).start();
////            // 用于服务器推送消息给用户
////            getMessage();
//            // 从客户端获取信息
//            BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            // 读取发来服务器信息
//            String line = null;
//            // 循环一直接收当前socket发来的消息
//            while (true) {
//                Thread.sleep(500);
//                // System.out.println("内容 : " + bff.readLine());
//                // 获取客户端的信息
//                while ((line = bff.readLine()) != null) {
//                    // 解析实体类
//                    MessageInfo messageInfo = gson.fromJson(line, MessageBean.class);
//                    // 将用户信息添加进入map中，模仿添加进数据库和内存
//                    // 实体类存入数据库，socket存入内存中，都以用户id作为参照
//                    setChatMap(messageBean, socket);
//                    // 将用户发送进来的消息转发给目标好友
//                    getFriend(messageBean);
//                    System.out.println("用户 : " + userMap.get(messageBean.getUserId()).getUserName());
//                    System.out.println("内容 : " + messageBean.getContent());
//                }
//            }
//            // server.close();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            System.out.println("错误 : " + e.getMessage());
//        }
//    }
//
//
//    /**
//     * 发送消息
//     */
//    private void getMessage() {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    String buffer;
//                    while (true) {
//                        // 从控制台输入
//                        BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
//                        buffer = strin.readLine();
//                        // 因为readLine以换行符为结束点所以，结尾加入换行
//                        buffer += "\n";
//                        // 这里修改成向全部连接到服务器的用户推送消息
//                        for (Socket socket : socketMap.values()) {
//                            OutputStream output = socket.getOutputStream();
//                            output.write(buffer.getBytes("utf-8"));
//                            // 发送数据
//                            output.flush();
//                        }
//                    }
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//}
