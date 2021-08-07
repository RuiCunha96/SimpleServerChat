package chat.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;

    private final int serverPort = 9696;

    private CopyOnWriteArrayList<ClientInfo> clients;

    private CopyOnWriteArrayList<Group> groups;


    public Server() {

        clients = new CopyOnWriteArrayList<>();

        ExecutorService PoolService = Executors.newCachedThreadPool();

        groups = new CopyOnWriteArrayList<>();
        groups.add(new Group("General"));

        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            try {
                Socket clientSocket = serverSocket.accept();

                ClientInfo user = new ClientInfo(clientSocket);

                LogIn login = new LogIn(user, this, PoolService);
                PoolService.submit(login);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init(ClientInfo user, ExecutorService PoolService) {

        System.out.println("entrou");
        clients.add(user);
        user.setGroupIn(groups.get(0));
        groups.get(0).addUser2Group(user);
        System.out.println(clients.size());
        PoolService.submit(new ServerListener(user, this));


    }


    public void broadcast(String message2Send, ClientInfo sender, boolean server2Speak) {

        if (!sender.getGroupIn().equals(groups.get(0))) {
            broadcast2Group(message2Send, sender, server2Speak);
            return;
        }

        //  PrintWriter out;
        for (ClientInfo client : clients) {

            if (client.equals(sender)) {
                continue;
            }

            // try {


            // out = new PrintWriter(client.getSocket().getOutputStream(), true);
            if (server2Speak) {
                client.send("Server : " + message2Send);
                // out.println("Server : " + message2Send);
            } else {
                client.send("General/ " + sender.getNickName() + ": " + message2Send);
                // out.println("General/ " + sender.getNickName() + ": " + message2Send);
            }

            //  } catch (IOException e) {
            //      e.printStackTrace();
            //  }

        }
    }

    private void broadcast2Group(String message2Send, ClientInfo sender, boolean server2Speak) {
        synchronized (groups) {

            for (ClientInfo clients : sender.getGroupIn().getClientsInGroup()) {
                if (clients.equals(sender)) {
                    continue;
                }

                if (server2Speak) {
                    clients.send("Server: " + message2Send);
                } else {
                    clients.send(sender.getGroupIn().getGroupName() + "/ " + sender.getNickName() + ": " + message2Send);
                }

            }

        }


//           /* for (int i = 0; i < groups.size(); i++) {
//
//                if (sender.getGroupIn().equals(groups.get(i))) {
//
//                    Group group2Send = groups.get(i);
//                    ClientInfo[] clientsInGroup = group2Send.getClientsInGroup();
//
//                    PrintWriter out;
//                    for (ClientInfo client : clientsInGroup) {
//
//                        if (client.equals(sender)) {
//                            continue;
//                        }
//
//                        try {
//                            out = new PrintWriter(client.getSocket().getOutputStream(), true);
//                            if (server2Speak) {
//                                out.println("Server: " + message2Send);
//                            } else {
//                                out.println(sender.getGroupIn().getGroupName() + "/ " + sender.getNickName() + ": " + message2Send);
//                            }
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }*/
    }


    public void createGroup(String groupName, ClientInfo clientCreator) {

        groups.add(new Group(groupName));

        message2Self(clientCreator, "You have created a new group - " + groupName);
        // broadcast(clientCreator.getName() + " has created " + groupName + " group.", clientCreator, true);
    }

    public void change2Group(ClientInfo client, String group2Go2String) {
        synchronized (groups) {


            //remove from previous group
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).equals(client.getGroupIn())) {
                    groups.get(i).remove(client); //remove no grupo
                }
            }

            //add to new group
            for (int i = 0; i < groups.size(); i++) {

                if (groups.get(i).getGroupName().equals(group2Go2String)) {

                    groups.get(i).addUser2Group(client);
                    client.setGroupIn(groups.get(i));

                    message2Self(client, "You have changed to " + groups.get(i).getGroupName() + " group.");
                    broadcast(client.getNickName() + " has moved to " + groups.get(i).getGroupName() + " group.", client, true);
                    return;
                }
            }

            message2Self(client, "That group does not exist.");
        }
    }


    public void message2Self(ClientInfo sender, String message2Send) {

        PrintWriter out = null;
        try {
            out = new PrintWriter(sender.getSocket().getOutputStream(), true);
            out.println(message2Send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeName(ClientInfo sender, String name) {

        String previousName = sender.getNickName();
        changeNameInFile(sender, name);
        sender.setNickName(name);

        message2Self(sender, "Server: Name changed to " + name);

        broadcast(previousName + " changed their name to " + sender.getNickName(), sender, true);

    }

    private void changeNameInFile(ClientInfo sender, String nickName) {

        try {

            BufferedReader bufferIn = new BufferedReader(new FileReader("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/Logins"));
            String userId;

            BufferedWriter bufferWriter = new BufferedWriter(new FileWriter("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/AuxFile"));

            while ((userId = bufferIn.readLine()) != null) {

                String[] someone = userId.split(":");


                if (someone[0].equals(sender.getName())) {
                    String nameAndPass = someone[0] + ":" + someone[1] + ":" + nickName;
                    System.out.println("entrou no gravanso do nome");
                    bufferWriter.write(nameAndPass, 0, nameAndPass.length());
                    bufferWriter.newLine();
                    continue;
                }
                bufferWriter.write(userId, 0, userId.length());
                bufferWriter.newLine();
            }
            bufferWriter.close();
            bufferIn.close();


            BufferedReader bufferIn2 = new BufferedReader(new FileReader("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/AuxFile"));
            BufferedWriter bufferWriter2 = new BufferedWriter(new FileWriter("/Users/codecadet/Desktop/MyMacRepo/mymacrepo/Modulo2/Threads/chat/src/chat/server/Logins"));
            String userId2;

            while ((userId2 = bufferIn2.readLine()) != null) {

                bufferWriter2.write(userId2, 0, userId2.length());
                bufferWriter2.newLine();
            }
            bufferWriter2.close();
            bufferIn2.close();

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }

    public void terminateConnection(ClientInfo client) {

        try {
            client.getSocket().close();

            clients.remove(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        broadcast(client.getNickName() + " has left the chat.", client, true);

    }


    public void whisper(ClientInfo sender, String receiverString, String message) {

        ClientInfo receiver = string2Client(receiverString);
        if (receiver == null) {
            message2Self(sender, "User not Found.");
            return;
        }

        PrintWriter out;
        for (ClientInfo client : clients) {

            if (client.equals(receiver)) {

                try {
                    out = new PrintWriter(client.getSocket().getOutputStream(), true);
                    out.println(sender.getNickName() + " wisppered to you : " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ClientInfo string2Client(String client) {

        for (ClientInfo possibleClient : clients) {
            if (possibleClient.getNickName().equals(client)) {
                return possibleClient;
            }

        }
        return null;
    }

    public void showGroups(ClientInfo sender) {

        String groupsInString = "\n Groups available:\n";

        for (Group group : groups) {

            groupsInString += "\t" + group.getGroupName() + "\n";
        }
        message2Self(sender, groupsInString);
    }

    public void usersInGroup(ClientInfo sender) {

        Group group = sender.getGroupIn();

        ClientInfo[] clientsInGroup = group.getClientsInGroup();

        String clientsInGroupString = "Users in Group: " + group.getGroupName() + "\n";

        for (int i = 0; i < clientsInGroup.length; i++) {
            clientsInGroupString += clientsInGroup[i].getNickName() + "\n";
        }
        message2Self(sender, clientsInGroupString);
    }


    public void groupIn(ClientInfo client) {
        message2Self(client, "" + client.getGroupIn().getGroupName());

    }

    public CopyOnWriteArrayList<ClientInfo> getClients() {
        return clients;
    }
}
