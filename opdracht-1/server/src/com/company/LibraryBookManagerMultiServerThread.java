package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Sven Westerlaken on 22-5-2017.
 */
public class LibraryBookManagerMultiServerThread extends Thread {
    private Socket socket;

    public LibraryBookManagerMultiServerThread(Socket socket) {
        super("LibraryBookManagerMultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(inputStreamReader);
        ) {

            String inputLine, outputLine;
            LibraryBookManagerProtocol protocol = new LibraryBookManagerProtocol();
            output.println(protocol.startup());

            while ((inputLine = input.readLine()) != null) {
                outputLine = protocol.processInput(inputLine);
                output.println(outputLine);
                if (outputLine.equals("Have a nice day!"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
