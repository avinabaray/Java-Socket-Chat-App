package com.avinabaray.chatapp.Client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
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
    private JScrollPane messageBoxScrollPane;

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
                // This ensures auto scrolling of the JTextArea
                messageBox.setCaretPosition(messageBox.getDocument().getLength());
            }
        });

        CONNECTButton.addActionListener(this);
        DISCONNECTButton.addActionListener(this);
        sendButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Thread buttonPress = new Thread(new Runnable() {
            @Override
            public void run() {
                if (event.getSource().equals(CONNECTButton)) {
//---------------------------------- CONNECT Btn Start ----------------------------------

                    String username = usernameText.getText().trim().replaceAll("\\s+", "").toUpperCase();
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

                    if (!clientApp.isConnected) {
                        messageBox.append("You are already DISCONNECTED\n");
                    } else {
                        clientApp.disconnect();
                    }

                } else if (event.getSource().equals(sendButton)) {
//---------------------------------- SEND_MSG Btn Start ----------------------------------

                    String msg = messageToSend.getText().trim();
                    if (!clientApp.isConnected) {
                        messageBox.append("Connect first\n");
                    } else if (!msg.isEmpty()) {
                        clientApp.broadcastMessage(msg);
                    }

                }
            }
        });
        buttonPress.start();
    }
}
