package com.avinabaray.chatapp.Client;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;
import com.avinabaray.chatapp.Models.MessageType;
import com.avinabaray.chatapp.Server.ClientHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class ClientApp {

    private String username = "user";
    private String prevUser = "";
    boolean isConnected = false;
    private static Vector<ClientHandler> activeUsers = new Vector<>();
    private OnClientDataUpdateListener clientUIListener;
    private ObjectOutputStream objOS;
    private ObjectInputStream objIS;
    private Socket sock;


    void setOnClientDataUpdateListener(OnClientDataUpdateListener lis) {
        this.clientUIListener = lis;
    }

    public void startClient(String username) throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);

        clientUIListener.onChatsUpdate("Requesting connection...");
        // getting host IP
        InetAddress ip = InetAddress.getByName(Constants.HOST_NAME);
        sock = new Socket(ip, Constants.PORT);

        // input and output streams
        objOS = new ObjectOutputStream(sock.getOutputStream());
        objIS = new ObjectInputStream(sock.getInputStream());

//        clientUIListener.onChatsUpdate("Enter new username (single word allowed): ");
//        username = sc.nextLine().trim();

        MessageModel userModel = new MessageModel();
        userModel.setMessageType(MessageType.USERNAME);
        userModel.setMessage(username);
        objOS.writeObject(userModel);

        userModel = null;
        try {
            userModel = (MessageModel) objIS.readObject();
            if (userModel.getMessage().equals(Constants.USER_EXISTS)) {
                clientUIListener.onChatsUpdate("username already exists...");
                clientUIListener.onChatsUpdate("Enter another Username");
                isConnected = false;
                sock.close();
//                startClient();
                return;
            } else if (userModel.getMessage().equals(Constants.NEW_USER)) {
                this.username = username;
                isConnected = true;
                clientUIListener.onChatsUpdate("New user created");
                clientUIListener.onChatsUpdate("Now you can Start Chatting");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
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
                            Vector<ClientHandler> activeUsersNow = ClientApp.activeUsers;
                            for (int i = 1; i <= activeUsersNow.size(); i++) {
                                ClientHandler ch = activeUsersNow.get(i - 1);
                                System.out.println(i + ". " + ch.name);
                            }

                            while (true) {
                                try {
                                    int userNo = -1;
                                    System.out.print("Enter user number you want to message: ");
                                    userNo = sc.nextInt();
                                    prevUser = activeUsers.get(userNo - 1).name;
//                                    sc.next();
                                    System.out.print("Enter your message: ");
                                    sc.nextLine();
                                    msg = sc.nextLine();
                                    sendMessage(username, prevUser, msg, objOS);
                                    break;
                                } catch (Exception e) {
                                    System.err.println("\nUser doesn't exist");
//                                    e.printStackTrace();
                                }
                            }

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
                MessageModel received;
                while (true) {
                    try {
                        received = (MessageModel) objIS.readObject();
                        switch (received.getMessageType()) {
                            case NORMAL_MSG:
                                clientUIListener.onChatsUpdate(received.getSender() + ": " + received.getMessage());
                                break;
                            case ACTIVE_USERS_LIST:
                                // To get the activeUsers Vector to the Client side
                                ClientApp.activeUsers = received.getActiveUsers();
                                break;
                        }
                    } catch (SocketException e) {
                        clientUIListener.onChatsUpdate("Server has stopped - Client Disconnected");
                        e.printStackTrace();
                        System.exit(0);
                    } catch (EOFException e) {
                        System.err.println("Disconnect by User Action");
                        clientUIListener.onChatsUpdate("DISCONNECTED SUCCESSFULLY");
                        isConnected = false;
                        e.printStackTrace();
                        break;
                    } catch (IOException | ClassNotFoundException e) {
                        clientUIListener.onChatsUpdate("Error fetching message: " + e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

//        sendMessageThread.start();
        readMessage.start();

    }

    private Vector<ClientHandler> getActiveUsers(ObjectOutputStream objOS, ObjectInputStream objIS) throws IOException {

        MessageModel sysMsg = new MessageModel();
        sysMsg.setSender(null);
        sysMsg.setMessage(Constants.GET_USERS_LIST);
        sysMsg.setReceiver(null);
        objOS.writeObject(sysMsg);

        return null;
    }

    private void sendMessage(String sender, String receiver, String message, ObjectOutputStream objOS) {
        MessageModel msgToSend = new MessageModel();
        msgToSend.setMessageType(MessageType.NORMAL_MSG);
        msgToSend.setSender(sender);
        msgToSend.setReceiver(receiver);
        msgToSend.setMessage(message);
        System.err.println("MSG: " + message);
        try {
            objOS.writeObject(msgToSend);
        } catch (IOException e) {
            clientUIListener.onChatsUpdate("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String msg) {
        sendMessage(username, null, msg, objOS);
    }

    public void disconnect() {
        MessageModel msgToSend = new MessageModel();
        msgToSend.setMessageType(MessageType.LOGOUT);
        try {
            objOS.writeObject(msgToSend);
        } catch (IOException e) {
            clientUIListener.onChatsUpdate("Error sending message: " + e.getMessage());
            clientUIListener.onChatsUpdate("DISCONNECT FAILED");
            e.printStackTrace();
        }


//        try {
//            sock.close();
//            isConnected = false;
//            clientUIListener.onChatsUpdate("DISCONNECTED SUCCESSFULLY");
//        } catch (IOException e) {
//            clientUIListener.onChatsUpdate("DISCONNECT FAILED");
//            e.printStackTrace();
//        }
    }

//    interface SetOnActiveUsersReceivedListener {
//        public void onActiveUsersReceived();
//    }

    interface OnClientDataUpdateListener {
        void onChatsUpdate(String message);
    }

}
