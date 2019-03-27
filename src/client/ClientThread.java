package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket;
    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader buf1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String echo1 = buf1.readLine();
            System.out.println(echo1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
