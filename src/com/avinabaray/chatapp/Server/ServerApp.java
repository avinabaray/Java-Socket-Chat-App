package com.avinabaray.chatapp.Server;

import com.avinabaray.chatapp.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerApp {

    public static Vector<ClientHandler> activeUsers = new Vector<>();
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

                    DataInputStream dataIS = new DataInputStream(currSock.getInputStream());
                    DataOutputStream dataOS = new DataOutputStream(currSock.getOutputStream());

                    boolean validUsername = true;
                    String username = dataIS.readUTF();
                    for (ClientHandler ch : ServerApp.activeUsers) {
                        if (ch.name.equals(username)) {
                            validUsername = false;
                            break;
                        }
                    }


                    if (validUsername) {
                        dataOS.writeInt(Constants.NEW_USER);
                        System.out.println("Assigning new handler for this client");
                        // creating a new thread object
                        ClientHandler clientHandler = new ClientHandler(currSock, username, objIS, objOS, dataIS, dataOS);
                        // Adding the user to the activeUsers vector
                        System.out.println("BOOO");
                        activeUsers.add(clientHandler);
                        System.out.println(activeUsers.size());
                        System.out.println(ServerApp.activeUsers.size());
                        // starting the thread
                        clientHandler.start();

                        // Incrementing user count
                        userNo++;
                    } else {
                        dataOS.writeInt(Constants.USER_EXISTS);
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
