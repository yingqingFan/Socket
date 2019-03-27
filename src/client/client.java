package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9999);
            Scanner scanner = new Scanner(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
