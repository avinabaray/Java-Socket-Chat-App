package com.avinabaray.chatapp.Server;

import javax.swing.*;

public class ServerUI {
    private JPanel serverPanel;
    private JButton STARTButton;
    private JButton STOPButton;
    private JTextArea textArea1;
    private JTextArea textArea2;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ServerUI");
        frame.setContentPane(new ServerUI().serverPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
