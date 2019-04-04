package com.ls.socket.util;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Test implements Serializable {
    private static final long serialVersionUID = -1406224361144906998L;
    private static Logger log = Logger.getLogger(com.ls.socket.util.Test.class);
    private String name;

    public Test(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
//        new DataUtil().writeToFile("c://logs/test.txt", new Test("qq"));
//        new DataUtil().writeToFile("c://logs/test.txt", new Test("bb"));
//        List<Test> list = new DataUtil().readFromFile("c://logs/test.txt", Test.class );
//        for (int i = 0; i < list.size(); i++) {
//            Test t = (Test)list.get(i);
//            System.out.println(t.getName());
//        }
//        System.out.println(SocketServer.class.getResource("/").getPath());

        List<String> a = new ArrayList<>();
        a.removeAll(a);
        System.out.println(a);
    }
}
