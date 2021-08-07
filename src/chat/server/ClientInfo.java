package chat.server;

import java.io.*;
import java.net.Socket;


public class ClientInfo {

    private Socket socket;
    private String nickName = "User";
    private String name;

    private PrintWriter out;
    private BufferedReader in;

    private Group groupIn;


    public ClientInfo(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Group getGroupIn() {
        return groupIn;
    }

    public void setGroupIn(Group groupIn) {
        this.groupIn = groupIn;
    }


    public void send(String message) {

        out.println(message);
    }

    public String receive(){
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InputStream getInputStream4Real(){
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public OutputStream getOutputStream4Real(){

        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }
}
