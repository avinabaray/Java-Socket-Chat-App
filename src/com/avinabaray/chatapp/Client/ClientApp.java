package com.avinabaray.chatapp.Client;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;
import com.avinabaray.chatapp.Server.ClientHandler;
import com.avinabaray.chatapp.Server.ServerApp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class ClientApp {

    private static String username = "user";
    private static String prevUser = "";
    private static SetOnActiveUsersReceivedListener setOnActiveUsersReceivedListener;
    private static Vector<ClientHandler> activeUsers = new Vector<>();

    public static void startClient() throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);

        // getting host IP
        InetAddress ip = InetAddress.getByName(Constants.HOST_NAME);

        Socket sock = new Socket(ip, Constants.PORT);
        // input and output streams
        DataInputStream dataIS = new DataInputStream(sock.getInputStream());
        DataOutputStream dataOS = new DataOutputStream(sock.getOutputStream());

        ObjectOutputStream objOS = new ObjectOutputStream(sock.getOutputStream());
        ObjectInputStream objIS = new ObjectInputStream(sock.getInputStream());

        System.out.print("Enter new username (single word allowed): ");
        username = sc.nextLine().trim();

        dataOS.writeUTF(username);
        if (dataIS.readInt() == Constants.USER_EXISTS) {
            System.out.println("username already exists...");
            sock.close();
            startClient();
            return;
        }


        // send message Thread
        Thread sendMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println();
                    System.out.println("Options:");
                    System.out.println(" 1. View online users");
                    System.out.println(" 2. Send message to prev user");
                    System.out.println(" 3. Exit");
                    System.out.print("\nEnter your choice: ");
                    int choice = sc.nextInt();

                    String msg;
                    switch (choice) {
                        case 1:
                            try {
                                activeUsers = getActiveUsers(objOS, objIS);
                            } catch (IOException e) {
                                System.err.println("Error fetching online users: " + e.getMessage());
//                                e.printStackTrace();
                                break;
                            }

                            System.out.println(ServerApp.activeUsers.size()); // Todo It is coming ZERO. Inspect it
                            for (int i = 1; i <= ServerApp.activeUsers.size(); i++) {
                                ClientHandler user = activeUsers.get(i);
                                System.out.println(" " + i + ". " + user.name);
                            }
                            System.out.print("Enter user number you want to message: ");
                            int userNo = sc.nextInt();
                            System.out.print("Enter your message: ");
                            msg = sc.nextLine();
                            prevUser = ServerApp.activeUsers.get(userNo).name;
                            sendMessage(username, prevUser, msg, objOS);

                            break;
                        case 2:
                            System.out.print("Enter your message: ");
                            msg = sc.nextLine();
                            sendMessage(username, prevUser, msg, objOS);
                            break;
                        case 3:
                            try {
                                sock.close();
                                System.out.println("Connection Terminated by User");
                                System.exit(0);
                            } catch (IOException e) {
                                System.err.println("I/O Error: " + e.getMessage());
                            }

                            break;
                        default:
                            System.out.println("Invalid Input");
                    }

                }
            }
        });

        // read message thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                MessageModel msgToReceive = new MessageModel();
                if (msgToReceive.getSender() != null) {
                    while (true) {
                        try {
                            msgToReceive = (MessageModel) objIS.readObject();
                            System.out.println(msgToReceive.getSender() + ": " + msgToReceive.getMessage());
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Error fetching message: " + e.getMessage());
                            break;
                        }
                    }
                } else {
                    if (msgToReceive.getMessage().equals(Constants.GET_USERS_LIST)) {
                        activeUsers = (Vector<ClientHandler>) msgToReceive.getObject();
                        setOnActiveUsersReceivedListener.onActiveUsersReceived();
                    }
                }
            }
        });

        sendMessageThread.start();
        readMessage.start();

    }

    private static Vector<ClientHandler> getActiveUsers(ObjectOutputStream objOS, ObjectInputStream objIS) throws IOException {

        MessageModel sysMsg = new MessageModel();
        sysMsg.setSender(null);
        sysMsg.setMessage(Constants.GET_USERS_LIST);
        sysMsg.setReceiver(null);
        objOS.writeObject(sysMsg);

        return null;
    }

    private static void sendMessage(String sender, String receiver, String message, ObjectOutputStream objOS) {
        MessageModel msgToSend = new MessageModel();
        msgToSend.setSender(sender);
        msgToSend.setReceiver(receiver);
        msgToSend.setMessage(message);
        try {
            objOS.writeObject(msgToSend);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    interface SetOnActiveUsersReceivedListener {
        public void onActiveUsersReceived();
    }
}