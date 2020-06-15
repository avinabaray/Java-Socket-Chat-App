package com.avinabaray.chatapp.Server;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;
import com.avinabaray.chatapp.Models.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler extends Thread implements Serializable {

    private transient Scanner sc = new Scanner(System.in);

    public String name;
    private transient ServerApp.OnServerDataUpdateListener serverUIListener;
    boolean isloggedin;
    transient final Socket currSock;
    transient final ObjectInputStream objIS;
    transient final ObjectOutputStream objOS;

    ClientHandler(Socket currSock, String name,
                  ObjectInputStream objIS, ObjectOutputStream objOS,
                  ServerApp.OnServerDataUpdateListener serverUIListener) {
        this.currSock = currSock;
        this.objIS = objIS;
        this.objOS = objOS;
        this.name = name;
        this.serverUIListener = serverUIListener;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        MessageModel received;
        while (true) {
            try {
                received = (MessageModel) objIS.readObject();
                switch (received.getMessageType()) {
                    case NORMAL_MSG:
                        serverUIListener.onChatsUpdate(
                                "Msg from " + received.getSender() +
                                " to " + received.getReceiver() +
                                ": " + received.getMessage());

                        MessageModel msgModelToSend = new MessageModel();
                        msgModelToSend.setMessageType(MessageType.NORMAL_MSG);
                        msgModelToSend.setMessage(received.getMessage());
                        msgModelToSend.setSender(received.getSender());
                        msgModelToSend.setReceiver(received.getReceiver());

                        for (ClientHandler ch : ServerApp.activeUsers) {
                            // if the recipient is found, write on its output stream
                            if (/*ch.name.equalsIgnoreCase(msgModelToSend.getReceiver()) && */ch.isloggedin) {
                                ch.objOS.writeObject(received);
//                            break;
                            }
                        }
                        break;
                    case BROADCAST_MSG:

                        break;
                    case ACTIVE_USERS_LIST:
                        objOS.writeObject(ServerApp.activeUsers);
                        break;
                    case LOGOUT:
//                        isloggedin = false;
//                        currSock.close();
//                        stop();
                        break;
                }

                if (received.getMessageType().equals(MessageType.LOGOUT)) {
                    isloggedin = false;
                    currSock.close();
                    serverUIListener.onChatsUpdate(name + " LOGGED OUT");
                    serverUIListener.onOnlineUsersUpdate();
                    break;
                }
//                if (received.getMessage().equalsIgnoreCase("logout")) {
//                    isloggedin = false;
//                    currSock.close();
//                    break;
//                }

//                if (received.getSender() != null) {
//                    MessageModel msgModelToSend = new MessageModel();
//                    msgModelToSend.setMessageType(MessageType.NORMAL_MSG);
//                    msgModelToSend.setMessage(received.getMessage());
//                    msgModelToSend.setSender(received.getSender());
//                    msgModelToSend.setReceiver(received.getReceiver());
//
//                    for (ClientHandler ch : ServerApp.activeUsers) {
//                        // if the recipient is found, write on its output stream
//                        if (/*ch.name.equalsIgnoreCase(msgModelToSend.getReceiver()) && */ch.isloggedin) {
//                            ch.objOS.writeObject(received);
////                            break;
//                        }
//                    }
//                } else {
//                    // Internal SIGNAL Message
//                    if (received.getMessage().equals(Constants.GET_USERS_LIST)) {
//                        objOS.writeObject(ServerApp.activeUsers);
//                    }
//                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }

        }

        try {
            // closing resources
            this.objIS.close();
            this.objOS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
