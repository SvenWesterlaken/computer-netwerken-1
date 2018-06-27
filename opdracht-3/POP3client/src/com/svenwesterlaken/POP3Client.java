package com.svenwesterlaken;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Sven Westerlaken on 4-6-2017.
 */
public class POP3Client extends Thread{

    private static final String HEADER_COMMAND = "TOP ";
    private static final String COUNT_COMMAND = "STAT";
    private static final String QUIT_COMMAND = "QUIT";
    private static final String USER_COMMAND = "USER ";
    private static final String PASSWORD_COMMAND = "PASS ";
    private static final String ERROR_RESPONSE = "-ERR ";

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void connect(String host, int port) throws IOException {
        socket = SSLSocketFactory.getDefault().createSocket();
        socket.connect(new InetSocketAddress(host, port));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        readResponseLine();
    }

    public void disconnect() throws IOException{
        if (!isConnected()) {
            throw new IllegalStateException("Not connected, can't disconnect");
        }

        socket.close();
        in = null;
        out = null;
    }

    protected String readResponseLine() throws IOException{
        String response = in.readLine();

//        System.out.println("C: " + response);

        if(response != null) {
            if (response.startsWith(ERROR_RESPONSE)) {
                throw new RuntimeException("An error ocurred: " + response.replaceFirst(ERROR_RESPONSE, ""));
            }
        }

        return response;
    }

    protected String sendRequest(String req) throws IOException {
        out.write(req + "\n");
        out.flush();
        return readResponseLine();
    }

    public void login(String username, String password) throws IOException {
        sendRequest(USER_COMMAND + username);
        sendRequest(PASSWORD_COMMAND + password);
    }

    public void logout() throws IOException {
        sendRequest(QUIT_COMMAND);
    }

    public int getMessageCount() throws IOException {
        String response = sendRequest(COUNT_COMMAND);
        int count = 1;


        if(response != null) {
            String[] values = response.split(" ");
            count = Integer.parseInt(values[1]);
        }

        return count;
    }

    public String getSubject(int i) throws IOException {
        sendRequest(HEADER_COMMAND + i + " 0");
        String response;
        boolean isSubject = false;
        StringBuilder sb = new StringBuilder();

        //Compose header
        while ((response = readResponseLine()).length() != 0) {
            if(response.toLowerCase().startsWith("subject:")) {
                if (response.length() > 9) {
                    sb.append(SubjectCodec.decode(response.substring(9)));
                    isSubject = true;
                }
            } else if (isSubject && SubjectCodec.isMultilineSubject(response)){
                //Subject is multi-lined
                sb.append(SubjectCodec.decode(response.replaceFirst(" ", "")));
            } else if (isSubject && !SubjectCodec.isMultilineSubject(response)) {
                //Subject is not multi-lined
                isSubject = false;
            }
        }

        if(sb.length() == 0) {
            sb.append("No subject");
        }

        return sb.toString();
    }

    public List<String> getSubjects() throws IOException {
        int messageCount = getMessageCount();
        List<String> subjects = new ArrayList<>();

        for(int i=1; i <= messageCount; i++) {
            subjects.add(getSubject(i));
        }

        return subjects;
    }

}
