package com.avinabaray.chatapp.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerUI extends JFrame {
    private JPanel serverPanel;
    private JButton STARTButton;
    private JButton STOPButton;
    private JTextArea chats;
    private JTextArea onlineUsers;

    private ServerApp serverApp;

    public ServerUI() {
        super("Server App");
        setContentPane(serverPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        serverApp = new ServerApp();
        serverApp.setOnServerDataUpdateListener(new ServerApp.OnServerDataUpdateListener() {
            @Override
            public void onChatsUpdate(String message) {
                chats.append(message + "\n");
                // This ensures auto scrolling of the JTextArea
                chats.setCaretPosition(chats.getDocument().getLength());
            }

            @Override
            public void onOnlineUsersUpdate() {
                // clearing old text
                onlineUsers.setText(null);
                for (ClientHandler ch : ServerApp.activeUsers) {
                    if (ch.isloggedin)
                        onlineUsers.append(ch.name + "\n");
                }
                onlineUsers.setCaretPosition(onlineUsers.getDocument().getLength());
            }
        });

        STARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ServerApp.isServerOn) {
                    Thread startServer = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            serverApp.startListening();
                        }
                    });
                    startServer.start();
                    ServerApp.isServerOn = true;
                } else {
                    chats.append("\nServer already RUNNING");
                }
            }
        });
        STOPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ServerApp.isServerOn) {
                    serverApp.stopListening();
                } else {
                    chats.append("\nServer already STOPPED");
                }
            }
        });
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("ServerUI");
//        frame.setContentPane(new ServerUI().serverPanel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }
}
