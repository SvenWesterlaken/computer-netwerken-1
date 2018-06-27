package com.svenwesterlaken;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int port = 110;
        String user = null, pass = null;

        if (args.length == 4) {
            //Specified port
            port = Integer.parseInt(args[1]);
            user = args[2];
            pass = args[3];
        } else if (args.length == 3) {
            //Use default port 110
            user = args[1];
            pass = args[2];
        } else {
            System.err.println("Use: java POP3Client <host> (<port>) <email> <password>");
            System.exit(1);
        }


        String host = args[0];
        POP3Client client = new POP3Client();

        try {
            client.connect(host, port);
            client.login(user, pass);

            int mailCount = client.getMessageCount();

            System.out.println(mailCount + " mails in the mailbox of '" + user + "'");
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            for (int i = 1; i <= mailCount; i++) {
                System.out.println((i) + ") " + client.getSubject(i));
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
            client.logout();
            System.out.println("Goodbye.");
            client.disconnect();
            System.exit(1);

        } catch (IOException e) {
            System.err.println("Could not connect to " + host + ", on port " + port);
            System.exit(-1);
        }



    }
}
