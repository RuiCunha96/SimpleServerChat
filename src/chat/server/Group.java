package chat.server;

import java.util.concurrent.CopyOnWriteArrayList;

public class Group {


    private CopyOnWriteArrayList<ClientInfo> peopleInGroup;
    private String groupName;

    public Group(String groupName) {

        peopleInGroup = new CopyOnWriteArrayList<>();

        this.groupName = groupName;

    }


    public void addUser2Group(ClientInfo client) {

        peopleInGroup.add(client);
    }

    public void remove (ClientInfo client){

        peopleInGroup.remove(client);

    }

    public String getGroupName() {
        return groupName;
    }

    public synchronized ClientInfo[] getClientsInGroup() {

        ClientInfo[] clients = new ClientInfo[peopleInGroup.size()];

        for (int i = 0; i < peopleInGroup.size(); i++) {

            clients[i] = peopleInGroup.get(i);

        }
        return clients;
    }

}
