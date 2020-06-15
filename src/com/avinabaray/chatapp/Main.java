package com.avinabaray.chatapp;

import com.avinabaray.chatapp.Client.ClientApp;
import com.avinabaray.chatapp.Client.ClientUI;
import com.avinabaray.chatapp.Server.ServerApp;
import com.avinabaray.chatapp.Server.ServerUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().getUserRole();
    }

    public void getUserRole() {
        System.out.println("Start:");
        System.out.println("1. Server App");
        System.out.println("2. Client App");
        System.out.print("\nEnter your choice: ");

        switch (sc.nextInt()) {
            case 1:
//                newServer();
//                ServerUI.main(new String[]{"arg"});
                new ServerUI();
                break;
            case 2:
                new ClientUI();
                break;
            default:
                System.out.println("Choose a valid option");
                getUserRole();
        }
    }

    private void newClient() {
        try {
            (new ClientApp()).startClient("user1");
        } catch (UnknownHostException e) {
            System.err.println("Host Address is invalid: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error in I/O: " + e.getMessage());
        }
    }

    private void newServer() {

//        ServerApp.startListening();

//        ServerSocket server = null;
//        Socket socket = null;
//        System.out.print("Enter port: ");
//        int port = sc.nextInt();
//        try {
//            server = new ServerSocket(port);
//            System.out.println("Server Started");
//            System.out.println("Waiting for a client");
//            socket = server.accept();
//            System.out.println("Client " + socket.getInetAddress() + " accepted");
//
//        } catch (BindException e) {
//            System.out.println("This PORT isn't available");
//            newServer();
//        } catch (IOException e) {
//
//        }

    }
}
