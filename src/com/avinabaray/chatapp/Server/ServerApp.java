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

                    System.out.println("Assigning new handler for this client");
                    // creating a new thread object
                    ClientHandler clientHandler = new ClientHandler(currSock, "user", objIS, objOS);
                    // Adding the user to the activeUsers vector
                    activeUsers.add(clientHandler);
                    // starting the thread
                    clientHandler.start();

                    // Incrementing user count
                    userNo++;

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
