package com.avinabaray.chatapp.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    private final DataInputStream is;
    private final DataOutputStream os;

    ClientHandler(Socket currSock, String name, DataInputStream is, DataOutputStream os) {
        this.currSock = currSock;
        this.is = is;
        this.os = os;
        this.name = name;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        super.run();
        String received;

        while (true) {

            try {
                received = is.readUTF();
                System.out.println(received);

                if (received.equalsIgnoreCase("logout")) {
                    isloggedin = false;
                    currSock.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "#");
                String msgToSend = st.nextToken();
                String recipient = st.nextToken();

                for (ClientHandler ch : ServerApp.activeUsers) {
                    // if the recipient is found, write on its output stream
                    if (ch.name.equalsIgnoreCase(recipient) && ch.isloggedin) {
                        ch.os.writeUTF(ch.name + " : " + msgToSend);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            // closing resources
            this.is.close();
            this.os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
