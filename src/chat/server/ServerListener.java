package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerListener implements Runnable {

    private ClientInfo client;
    private BufferedReader in;
    private Server server;

    public ServerListener(ClientInfo client, Server server) {
        this.server = server;
        this.client = client;

        try {
            this.in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        while (!client.getSocket().isClosed()) {
            String message = null;
            try {
                message = in.readLine();

                if (message.startsWith("/")) {

                    String[] command = message.split(" ");

                    switch (command[0]) {

                        case "/help":

                            server.message2Self(client, "Server: \n\tChange Name: /name newName\n\t" +
                                    "Create Group: /c GroupName\n\t" +
                                    "Move 2 Group: /m GroupName\n\t" +
                                    "Whisper to user: /w receiverName message\n\t" +
                                    "See groups available: /show\n\t" +
                                    "Current Group : /now\n\t" +
                                    "Send 2 General : /gen message\n\t" +
                                    "quit : /quit \n\t" +
                                    "Users in Group: /users");
                            continue;

                        case "/name":
                            server.changeName(client, command[1]);// atualizar dados no txt
                            continue;

                        case "/quit":
                            client.send("Quiting...");
                            server.getClients().remove(client);
                            server.terminateConnection(client);
                            continue;

                        case "/c": // creat group
                            server.createGroup(command[1], client);
                            continue;

                        case "/m"://move 2 group
                            server.change2Group(client, command[1]);
                            continue;

                        case "/w":
                            //whisper name message
                            String messageW = "";
                            for (int i = 2; i < command.length; i++) {
                                messageW += command[i] + " ";
                            }

                            server.whisper(client, command[1], messageW);
                            continue;

                        case "/show":
                            server.showGroups(client);
                            continue;

                        case "/users"://users in group
                            server.usersInGroup(client);
                            continue;

                        case "/now":// current group
                            server.groupIn(client);
                            continue;

                        case "/gen":
                            String messageGen = "";
                            for (int i = 1; i < command.length; i++) {
                                messageGen += command[i] + " ";
                            }
                            server.broadcast(messageGen, client, false);
                            continue;

                        default:
                            server.message2Self(client, "Server: Wrong command!\nFor HELP type: /help");
                            continue;
                    }
                }
                server.broadcast(message, client, false);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
