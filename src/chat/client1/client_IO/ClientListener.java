package chat.client1.client_IO;

import chat.client1.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class ClientListener implements Runnable {


    private InetAddress serverAddress;
    private int serverPort;
    private BufferedReader in;
    private Socket clientSocket;
    private Client client;



    public ClientListener(Socket clientSocket, Client client) {

        this.client = client;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (!clientSocket.isClosed()) {

            try {
                String messageIn = in.readLine();

                System.out.println(messageIn);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
