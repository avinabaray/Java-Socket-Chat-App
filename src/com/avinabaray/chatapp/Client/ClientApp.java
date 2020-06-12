package com.avinabaray.chatapp.Client;

import com.avinabaray.chatapp.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApp {
    public static void startClient() throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);

        // getting host IP
        InetAddress ip = InetAddress.getByName(Constants.HOST_NAME);

        Socket sock = new Socket(ip, Constants.PORT);
        // input and output streams
        DataInputStream is = new DataInputStream(sock.getInputStream());
        DataOutputStream os = new DataOutputStream(sock.getOutputStream());



        // send message Thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                String msgToSend;
                while (true) {
                    msgToSend = sc.nextLine();
                    try {
                        os.writeUTF(msgToSend);
                    } catch (IOException e) {
                        System.err.println("Error fetching message: " + e.getMessage());
                    }
                }
            }
        });

        // read message thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                String msgToReceive;
                while (true) {
                    try {
                        msgToReceive = is.readUTF();
                        System.out.println(msgToReceive);
                    } catch (IOException e) {
                        System.err.println("Error fetching message: " + e.getMessage());
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
