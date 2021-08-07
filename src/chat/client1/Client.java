package chat.client1;

import chat.client1.client_IO.ClientSender;
import chat.client1.client_IO.ClientListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private String name;
    private InetAddress serverAddress;
    private final int serverPort;
    private Socket clientSocket;
    private ClientSender sendIt;
    private ClientListener listen;

    private boolean menu = false;

    private boolean logIn = false;
    private boolean signIgn = false;

    public Client(String name) {

        try {

            this.serverAddress = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            System.out.println("quase");
        }

        this.name = name;
        this.serverPort = 9696;

        ExecutorService cachedPoolService = Executors.newCachedThreadPool();

        try {
            this.clientSocket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendIt = new ClientSender(clientSocket, this);
        listen = new ClientListener(clientSocket, this);
        cachedPoolService.submit(sendIt);
        cachedPoolService.submit(listen);

    }

    public void closeAll() {
        sendIt.close();
        listen.close();
        System.exit(1);

    }

    public boolean isLogedIn() {
        return logIn;
    }

    public void setLogIn(boolean logIn) {
        this.logIn = logIn;
    }

    public boolean isSignedign() {
        return signIgn;
    }

    public void setSignIn(boolean signSign) {
        this.signIgn = signSign;
    }

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }
}
