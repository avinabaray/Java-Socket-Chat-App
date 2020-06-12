package com.avinabaray.chatapp;

import com.avinabaray.chatapp.Server.ServerApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
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
        System.out.println("\nEnter your choice:");

        switch (sc.nextInt()) {
            case 1:
                newServer();
                break;
            case 2:
                newClient();
                break;
            default:
                System.out.println("Choose a valid option");
        }
    }

    private void newClient() {

    }

    private void newServer() {

        ServerApp.startListening();

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
