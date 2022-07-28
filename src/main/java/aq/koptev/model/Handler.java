package aq.koptev.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler {
    private Server server;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Identifier identifier;
    private User user;
    public Handler(Server server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        identifier = new Identifier(this, clientSocket);
    }

    public void processHandle() {
        Thread thread = new Thread(() -> {
            try {
                processIdentification();
                waitMessages();
            } catch (IOException e) {
                try {
                    processDisconnectHandler();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void processIdentification() throws IOException {
        identifier.processIdentification();
    }

    private void waitMessages() throws IOException {
        while(true) {
            String incomingString = inputStream.readUTF();
            if(!incomingString.startsWith("#")) {
                System.out.println("Bad command");
            }
            Command command = Command.getCommandByValue(incomingString.split("\\s+", 2)[0]);
            String message = incomingString.split("\\s+", 2)[1];
            server.processMessage(command, message);
        }
    }

    private synchronized void processDisconnectHandler() throws IOException {
        sendDisconnectedUserLogin();
        disconnectHandler();
    }

    private void sendDisconnectedUserLogin() {
        if(user != null) {
            server.processMessage(Command.CLIENT_DISCONNECT_COMMAND, String.format("Пользователь %s покинул чат", user.getLogin()));
        }
    }

    private void disconnectHandler() throws IOException {
        closeConnection();
        server.removeHandler(this);
    }

    private void closeConnection() throws IOException {
        clientSocket.close();
    }

    public void sendMessage(Command command, String message) throws IOException {
        String send = String.format("%s %s", command.getCommand(), message).trim();
        outputStream.writeUTF(send);
    }

    public void sendMessage(Command command, String sender, String message) throws IOException {
        String send = String.format("%s %s %s", command.getCommand(), sender, message).trim();
        outputStream.writeUTF(send);
    }

    public void sendMessage(Command command, String sender, String receiver, String message) throws IOException {
        String send = String.format("%s %s %s %s", command.getCommand(), sender, receiver, message).trim();
        outputStream.writeUTF(send);
    }

    public boolean isUserConnected(String login) {
        return server.checkConnectionClientByLogin(login);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void registrationHandler() {
        server.addHandler(this);
    }
}
