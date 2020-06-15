package com.avinabaray.chatapp.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class ClientUI extends JFrame implements ActionListener {
    private JTextArea messageToSend;
    private JButton sendButton;
    private JTextArea messageBox;
    private JPanel clientPanel;
    private JTextField usernameText;
    private JButton CONNECTButton;
    private JButton DISCONNECTButton;

    private ClientApp clientApp;

    public ClientUI() {
        super("ClientUI");
        setContentPane(clientPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        messageBox.append("Enter new username (single word allowed)\n");
        clientApp = new ClientApp();
        clientApp.setOnClientDataUpdateListener(new ClientApp.OnClientDataUpdateListener() {
            @Override
            public void onChatsUpdate(String message) {
                messageBox.append(message + "\n");
            }
        });

        CONNECTButton.addActionListener(this);
        DISCONNECTButton.addActionListener(this);
        sendButton.addActionListener(this);

//        CONNECTButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });

//        sendButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Thread buttonPress = new Thread(new Runnable() {
            @Override
            public void run() {
                if (event.getSource().equals(CONNECTButton)) {
//---------------------------------- CONNECT Btn Start ----------------------------------

                    String username = usernameText.getText().trim().replaceAll("\\s+", "");
                    if (username.isEmpty()) {
                        messageBox.append("USERNAME CAN'T BE EMPTY\n");
                    } else if (clientApp.isConnected) {
                        messageBox.append("You are already CONNECTED\n");
                    } else {
                        try {
                            clientApp.startClient(username);
                        } catch (UnknownHostException e) {
                            messageBox.append("Host Address is invalid: " + e.getMessage() + "\n");
                            e.printStackTrace();
                        } catch (ConnectException e) {
                            messageBox.append("Server refused to Connect\n");
                            e.printStackTrace();
                        } catch (IOException e) {
                            System.err.println("Error in I/O: " + e.getMessage() + "\n");
                            e.printStackTrace();
                        }
                    }

                } else if (event.getSource().equals(DISCONNECTButton)) {
//---------------------------------- DISCONNECT Btn Start ----------------------------------

//                    System.out.println("PRESSEDQQ - " + messageToSend.getText() + ".");

                } else if (event.getSource().equals(sendButton)) {
//---------------------------------- SEND_MSG Btn Start ----------------------------------

//                    System.out.println("PRESSED - " + messageToSend.getText() + ".");
                    String msg = messageToSend.getText().trim();
                    if (!clientApp.isConnected) {
                        messageBox.append("Connect first");
                    } else if (!msg.isEmpty()) {
                        clientApp.broadcastMessage(msg);
                    }

                }
            }
        });
        buttonPress.start();
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("ClientUI");
//        frame.setContentPane(new ClientUI().clientPanel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }
}
