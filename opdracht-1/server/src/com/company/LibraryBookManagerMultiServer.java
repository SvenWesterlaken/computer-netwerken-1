package com.company;

import java.io.IOException;
import java.net.ServerSocket;

public class LibraryBookManagerMultiServer {

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Use: java LibraryBookManagerMultiServer <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket ss = new ServerSocket(port)) {
            while (listening) {
                new LibraryBookManagerMultiServerThread(ss.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

    }
}
