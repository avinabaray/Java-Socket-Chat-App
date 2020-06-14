package com.avinabaray.chatapp.Server;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;
import com.avinabaray.chatapp.Models.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerApp {

    static Vector<ClientHandler> activeUsers = new Vector<>();
    private static int userNo = 1;

    public static void startListening() {
        try {
            ServerSocket serverSock = new ServerSocket(Constants.PORT);
            System.out.println("Server Started");
            System.out.println("Waiting for a client");
            while (true) {

                Socket currSock = null;
                try {
                    currSock = serverSock.accept();
                    System.out.println("New client is connected : " + currSock);

                    // input and output streams
                    ObjectOutputStream objOS = new ObjectOutputStream(currSock.getOutputStream());
                    ObjectInputStream objIS = new ObjectInputStream(currSock.getInputStream());

//                    DataInputStream dataIS = new DataInputStream(currSock.getInputStream());
//                    DataOutputStream dataOS = new DataOutputStream(currSock.getOutputStream());

                    boolean validUsername = true;
                    String username;
                    MessageModel userModel = (MessageModel) objIS.readObject();
                    if (userModel.getMessageType() == MessageType.USERNAME) {
                        username = userModel.getMessage();
                        for (ClientHandler ch : ServerApp.activeUsers) {
                            if (ch.name.equals(username)) {
                                validUsername = false;
                                break;
                            }
                        }
                    } else {
                        System.err.println("Username not defined");
                        continue;
                    }

                    MessageModel sysMsg = new MessageModel();
                    sysMsg.setMessageType(MessageType.USERNAME);
                    if (validUsername) {
                        sysMsg.setMessage(Constants.NEW_USER);
                        objOS.writeObject(sysMsg);
                        System.out.println("Assigning new handler for this client");
                        System.err.println(username + " added");
                        // creating a new thread object
                        ClientHandler clientHandler = new ClientHandler(currSock, username, objIS, objOS);
                        // Adding the user to the activeUsers vector
                        activeUsers.add(clientHandler);
                        // starting the thread
                        clientHandler.start();

                        for (ClientHandler ch : activeUsers) {
                            MessageModel activeUsersBroadcast = new MessageModel();
                            activeUsersBroadcast.setMessageType(MessageType.ACTIVE_USERS_LIST);
                            activeUsersBroadcast.setObject(activeUsers);
                            objOS.writeObject(activeUsersBroadcast);
                        }

                        System.out.println("Active Users Updated to all active clients");

                        // Incrementing user count
                        userNo++;
                    } else {
                        sysMsg.setMessage(Constants.USER_EXISTS);
                        objOS.writeObject(sysMsg);
                    }

                } catch (Exception e) {
                    currSock.close();
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
