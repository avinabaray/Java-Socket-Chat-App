package com.avinabaray.chatapp.Server;

import com.avinabaray.chatapp.Constants;
import com.avinabaray.chatapp.Models.MessageModel;
import com.avinabaray.chatapp.Models.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

/**
 * @author Avinaba Ray
 */
public class ServerApp {

    static Vector<ClientHandler> activeUsers = new Vector<>();
    static boolean isServerOn = false;
    private static int userNo = 1;
    private ServerSocket serverSock;
    private OnServerDataUpdateListener serverUIListener;

    void setOnServerDataUpdateListener(OnServerDataUpdateListener lis) {
        this.serverUIListener = lis;
    }

    public void startListening() {
        try {
            serverSock = new ServerSocket(Constants.PORT);
            if (Constants.debug) {
                System.out.println("Server Started");
                System.out.println("Waiting for a client");
            }
            serverUIListener.onChatsUpdate("Server Started");
            serverUIListener.onChatsUpdate("Waiting for a client");
            while (true) {

                Socket currSock = null;
                try {
                    currSock = serverSock.accept();
                    if (Constants.debug)
                        System.out.println("New client is connected : " + currSock);
                    serverUIListener.onChatsUpdate("New client is connected : " + currSock);

                    // input and output streams
                    ObjectOutputStream objOS = new ObjectOutputStream(currSock.getOutputStream());
                    ObjectInputStream objIS = new ObjectInputStream(currSock.getInputStream());

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
                        if (Constants.debug)
                            System.err.println("Username not defined");
                        serverUIListener.onChatsUpdate("Username not defined");
                        continue;
                    }

                    MessageModel sysMsg = new MessageModel();
                    sysMsg.setMessageType(MessageType.USERNAME);
                    if (validUsername) {
                        sysMsg.setMessage(Constants.NEW_USER);
                        objOS.writeObject(sysMsg);
                        serverUIListener.onChatsUpdate("Assigning new handler for this client");

                        // creating a new thread object
                        ClientHandler clientHandler = new ClientHandler(currSock, username, objIS, objOS, serverUIListener);
                        // Adding the user to the activeUsers vector
                        activeUsers.add(clientHandler);
                        // starting the thread
                        clientHandler.start();

                        // Updating Online users
                        serverUIListener.onOnlineUsersUpdate();

                        MessageModel activeUsersBroadcast = new MessageModel();
                        activeUsersBroadcast.setMessageType(MessageType.ACTIVE_USERS_LIST);
                        activeUsersBroadcast.setActiveUsers(activeUsers);
                        for (ClientHandler ch : activeUsers) {
                            if (ch.isloggedin)
                                ch.objOS.writeObject(activeUsersBroadcast);
                        }

                        serverUIListener.onChatsUpdate("Active Users Updated to all active clients");

                        // Incrementing user count, presently not in use though
                        userNo++;
                    } else {
                        sysMsg.setMessage(Constants.USER_EXISTS);
                        objOS.writeObject(sysMsg);
                    }

                } catch (SocketException e) {
                    serverUIListener.onChatsUpdate("SERVER HAS STOPPED");
                    serverUIListener.onChatsUpdate("All clients disconnected");
                    isServerOn = false;
                    activeUsers.clear();
                    if (Constants.debug)
                        e.printStackTrace();
                    break;
                } catch (Exception e) {
                    if (Constants.debug)
                        e.printStackTrace();
                    if (currSock != null) {
                        currSock.close();
                    }
                }

            }
        } catch (IOException e) {
            if (Constants.debug)
                e.printStackTrace();
        }

    }

    public void stopListening() {
        serverUIListener.onChatsUpdate("SERVER STOPPING...");
        try {
            serverSock.close();
            for (ClientHandler ch : activeUsers) {
                ch.currSock.close();
            }
        } catch (IOException e) {
            if (Constants.debug) {
                System.out.println("ServerSocket didn't close");
                e.printStackTrace();
            }
        }
    }

    interface OnServerDataUpdateListener {
        void onChatsUpdate(String message);
        void onOnlineUsersUpdate();
    }
}
