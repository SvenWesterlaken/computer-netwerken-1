package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class LibraryBookManagerClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Use: java LibraryBookManagerClient <host name> <port number>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(host, port);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                InputStreamReader reader = new InputStreamReader(socket.getInputStream());
                BufferedReader input = new BufferedReader(reader);
        ) {

            InputStreamReader consoleReader = new InputStreamReader(System.in);
            BufferedReader consoleInput = new BufferedReader(consoleReader);
            String request, response;

            while ((response = input.readLine()) != null) {
                System.out.println("S: " + response);

                if (response.equals("Have a nice day!")) {
                    break;
                }

                request = consoleInput.readLine();
                if (request != null) {
                    output.println(request);
                }
            }



        } catch (UnknownHostException e) {
            System.err.println("Couldn't find host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host: " + host);
            System.exit(1);
        }
    }
}
