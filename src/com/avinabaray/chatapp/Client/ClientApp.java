package com.avinabaray.chatapp.Client;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApp {

    private static String username = "user";

    public static void startClient() throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);

        // getting host IP
        InetAddress ip = InetAddress.getByName(Constants.HOST_NAME);

        Socket sock = new Socket(ip, Constants.PORT);
        // input and output streams
        DataInputStream is = new DataInputStream(sock.getInputStream());
        DataOutputStream os = new DataOutputStream(sock.getOutputStream());

        ObjectOutputStream objOS = new ObjectOutputStream(sock.getOutputStream());
        ObjectInputStream objIS = new ObjectInputStream(sock.getInputStream());



        // send message Thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                MessageModel msgToSend = new MessageModel();
                while (true) {
                    msgToSend.setSender(username);
                    msgToSend.setMessage(sc.nextLine());
                    msgToSend.setReceiver("user");
                    try {
//                        os.writeUTF(msgToSend);
                        objOS.writeObject(msgToSend);
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
                MessageModel msgToReceive = new MessageModel();
                while (true) {
                    try {
//                        msgToReceive = is.readUTF();
//                        System.out.println(msgToReceive);
                        msgToReceive = (MessageModel) objIS.readObject();
                        System.out.println(msgToReceive.getSender() + ": " + msgToReceive.getMessage());
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Error fetching message: " + e.getMessage());
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
