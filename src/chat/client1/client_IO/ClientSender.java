package chat.client1.client_IO;

import chat.client1.Client;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientSender implements Runnable {

    private InetAddress serverAddress;
    private int serverPort;
    private PrintWriter out;
    private Socket clientSocket;
    private Client client;


    public ClientSender(Socket clientSocket, Client client) {

        this.client = client;

        try {
            this.clientSocket = clientSocket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String message2Send;

        while (true) {

            message2Send = consoleIn();
            out.println(message2Send);
        }
    }


    private String consoleIn() {

        BufferedReader inConsole = new BufferedReader(new InputStreamReader(System.in));
        String message = null;
        try {
            message = inConsole.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;

    }



    public void close() {
        out.close();
    }
}
