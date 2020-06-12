package com.avinabaray.chatapp.Server;

import com.avinabaray.chatapp.Models.MessageModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

class ClientHandler extends Thread {

//    private DateFormat forDate = new SimpleDateFormat("dd/MM/yyyy");
//    private DateFormat forTime = new SimpleDateFormat("hh:mm:ss");

    private Scanner sc = new Scanner(System.in);

    private String name;
    boolean isloggedin;
    private final Socket currSock;
    private final ObjectInputStream objIS;
    private final ObjectOutputStream objOS;

    ClientHandler(Socket currSock, String name, ObjectInputStream objIS, ObjectOutputStream objOS) {
        this.currSock = currSock;
        this.objIS = objIS;
        this.objOS = objOS;
        this.name = name;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        super.run();

        while (true) {

            try {
                MessageModel received = (MessageModel) objIS.readObject();
                System.out.println(received.getMessage());

                if (received.getMessage().equalsIgnoreCase("logout")) {
                    isloggedin = false;
                    currSock.close();
                    break;
                }

//                StringTokenizer st = new StringTokenizer(received, "#");
//                String msgToSend = st.nextToken();
//                String recipient = st.nextToken();

                MessageModel msgModelToSend = new MessageModel();
                msgModelToSend.setMessage(received.getMessage());
                msgModelToSend.setSender(received.getSender());
                msgModelToSend.setReceiver(received.getReceiver());

                for (ClientHandler ch : ServerApp.activeUsers) {
                    // if the recipient is found, write on its output stream
                    if (ch.name.equalsIgnoreCase(msgModelToSend.getReceiver()) && ch.isloggedin) {
                        ch.objOS.writeObject(msgModelToSend);
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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
