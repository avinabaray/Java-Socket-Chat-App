package com.avinabaray.chatapp;

import com.avinabaray.chatapp.Client.ClientUI;
import com.avinabaray.chatapp.Server.ServerUI;

import java.util.Scanner;

/**
 * @author Avinaba Ray
 */
public class Main {

    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().getUserRole();
    }

    public void getUserRole() {
        System.err.println("Java Socket Chat App");
        System.out.println("  Name: Avinaba Ray (CSE-J) Sem-4");
        System.out.println("  Registration No: 1841012165");
        System.out.println("  ITER, S'O'A Deemed to be University");
        System.out.println("\nStart:");
        System.out.println("1. Server App");
        System.out.println("2. Client App");
        System.out.println("3. EXIT");
        System.out.print("\nEnter your choice: ");

        while (true) {
            try {
                switch (sc.nextInt()) {
                    case 1:
                        new ServerUI();
                        break;
                    case 2:
                        new ClientUI();
                        break;
                    case 3:
                        System.exit(0);
                    default:
                        System.err.println("Choose a valid option");
                        System.out.print("\nEnter your choice: ");
                        getUserRole();
                }
            } catch (Exception e) {
                System.err.println("Enter Number only");
                System.out.print("\nEnter your choice: ");
                sc.next();
                if (Constants.debug)
                    e.printStackTrace();
            }
        }
    }
}
