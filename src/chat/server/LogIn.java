package chat.server;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.PasswordInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.util.concurrent.ExecutorService;

public class LogIn implements Runnable {

    private ClientInfo client;
    private boolean isToLogin = false;
    private Server server;
    private ExecutorService PoolService;

    public LogIn(ClientInfo client, Server server, ExecutorService PoolService) {
        this.PoolService = PoolService;
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {

         initialMenu(client);

    }


    private boolean signIn(ClientInfo client) {


        Prompt prompt = new Prompt(client.getInputStream4Real(), new PrintStream(client.getOutputStream4Real()));

        StringInputScanner stringInputScanner = new StringInputScanner();

        PasswordInputScanner passwordInputScanner = new PasswordInputScanner();


        stringInputScanner.setMessage("New UserName: ");
        passwordInputScanner.setMessage("Pass: ");

        String newUser = prompt.getUserInput(stringInputScanner);

        int pass = prompt.getUserInput(passwordInputScanner).hashCode();

        if (isUsernameValid(newUser)) {
            saveNewUser(newUser, pass, client.getNickName());
            client.send("You have been Registered.");
            return true;

        } else {
            client.send("User Name already exists.");
            initialMenu(client);
            return false;
        }


    }

    private void initialMenu(ClientInfo client) {

        client.send("/menu");


        Prompt prompt = new Prompt(client.getInputStream4Real(), new PrintStream(client.getOutputStream4Real()));

        String[] options = {"log In", "sign Up","exit"};

        MenuInputScanner scanner = new MenuInputScanner(options);
        scanner.setMessage("Hello you have reached my Chatyy!");

        String choice =  prompt.getUserInput(scanner) + "";


        if (choice.equals("1")) {
            isToLogin = logAttempt(client);

        } else if (choice.equals("2")) {

            if (signIn(client)) {
                isToLogin = logAttempt(client);
            }

        }else if (choice.equals("3")){
            try {
                client.send("You have exited.");
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            try {
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(isToLogin);

        if (isToLogin) {
            server.init(client, PoolService);
            return;
        }

        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean logAttempt(ClientInfo client) {

        Prompt prompt = new Prompt(client.getInputStream4Real(), new PrintStream(client.getOutputStream4Real()));

        StringInputScanner stringInputScanner = new StringInputScanner();
        PasswordInputScanner passwordInputScanner = new PasswordInputScanner();


        int counter = 3;

        while (counter-- > 0) {

            stringInputScanner.setMessage("UserName: ");
            passwordInputScanner.setMessage("Pass: ");

            String userName = prompt.getUserInput(stringInputScanner);
            int pass = (prompt.getUserInput(passwordInputScanner)).hashCode();

            if (isUserValid(userName, pass, client)) {
                client.send("Server: Login successful.");
                client.setName(userName);

                return true;
            }

            client.send("Server: Login Failed. you have " + counter + " attempts.");
        }
        client.send("Server: Too many attempts.");
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }


    private Boolean isUserValid(String userName, int pass, ClientInfo client) {

        try {

            BufferedReader bufferIn = new BufferedReader(new FileReader("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/Logins"));
            String userId;

            while ((userId = bufferIn.readLine()) != null) {

                String[] someone = userId.split(":");


                if (someone[0].equals(userName) && Integer.parseInt(someone[1]) == (pass)) {
                    client.setNickName(someone[2]);
                    bufferIn.close();
                    return true;
                }
            }

            bufferIn.close();

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        return false;
    }


    private boolean isUsernameValid(String username) {

        try {

            BufferedReader bufferIn = new BufferedReader(new FileReader("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/Logins"));
            String userId;

            while ((userId = bufferIn.readLine()) != null) {

                String[] someone = userId.split(":");

                if (someone[0].equals(username)) {
                    return false;
                }

            }

            bufferIn.close();

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        return true;
    }

    private void saveNewUser(String username, int pass, String nickname) {

        BufferedWriter bufferWriter;

        {
            try {
                bufferWriter = new BufferedWriter(new FileWriter("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/Logins", true));

                String nameAndPass = username + ":" + pass + ":" + nickname;
                bufferWriter.write(nameAndPass, 0, nameAndPass.length());
                bufferWriter.newLine();

                bufferWriter.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

