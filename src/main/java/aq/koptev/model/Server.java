package aq.koptev.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int DEFAULT_PORT = 5082;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int port;
    private List<Handler> handlers;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        handlers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitConnection() throws IOException {
        while(true) {
            System.out.println("Server wait connection...");
            clientSocket = serverSocket.accept();
            System.out.println("Connection is success!");
            Handler handler = new Handler(this, clientSocket);
            handler.processHandle();
        }
    }

    public synchronized void processMessage(Command command, String message) {
        switch (command) {
            case COMMON_MESSAGE_COMMAND:
                processCommonMessage(message);
                break;
            case PRIVATE_MESSAGE_COMMAND:
                processPrivateMessage(message);
                break;
            case NOTIFY_CLIENTS_ON_CLIENT_CONNECT_COMMAND:
                processClientConnectedMessage(message);
                break;
            case CLIENT_DISCONNECT_COMMAND:
                processClientDisconnectedMessage(message);
                break;
            case REQUEST_CONNECTED_CLIENTS_COMMAND:
                processRequestConnectedClients();
                break;
            case REQUEST_CONNECTED_CLIENT_COMMAND:
                processRequestConnectedClient(message);
                break;
        }
    }

    private void processCommonMessage(String message) {
        String sender = message.split("\\s+", 2)[0];
        String textMessage = message.split("\\s+", 2)[1];
        for(Handler handler : handlers) {
            try {
                handler.sendMessage(Command.COMMON_MESSAGE_COMMAND, sender, textMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processPrivateMessage(String message) {
        String sender = message.split("\\s+", 3)[0];
        String receiver = message.split("\\s+", 3)[1];
        String textMessage = message.split("\\s+", 3)[2];
        for(Handler handler : handlers) {
            sendPrivateMessage(handler, sender, receiver, textMessage);
        }
    }

    private void sendPrivateMessage(Handler handler, String sender, String receiver, String message) {
        if(handler.getUser().getLogin().equals(receiver) || handler.getUser().getLogin().equals(sender)) {
            try {
                handler.sendMessage(Command.PRIVATE_MESSAGE_COMMAND, sender, message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processClientConnectedMessage(String message) {
        for(Handler serverHandler : handlers) {
            try {
                serverHandler.sendMessage(Command.CLIENT_CONNECT_COMMAND, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClientDisconnectedMessage(String message) {
        for(Handler serverHandler : handlers) {
            try {
                serverHandler.sendMessage(Command.CLIENT_DISCONNECT_COMMAND, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequestConnectedClients() {
        String users = "";
        for(Handler handler : handlers) {
            users += String.format("%s ", handler.getUser().getLogin());
        }
        for(Handler handler : handlers) {
            try {
                handler.sendMessage(Command.ANSWER_CONNECTED_CLIENTS_COMMAND, users);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processRequestConnectedClient(String message) {
        String login = message.split("\\s+", 2)[0];
        String password = message.split("\\s+", 2)[1];
        for(Handler handler : handlers) {
            if(handler.getUser().getLogin().equals(login)) {
                String send = String.format("%s %s", login, password);
                try {
                    handler.sendMessage(Command.ANSWER_CONNECTED_CLIENT_COMMAND, send);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public boolean checkConnectionClientByLogin(String login) {
        for(Handler handler : handlers) {
            if(login.equals(handler.getUser().getLogin())) {
                return true;
            }
        }
        return false;
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public void removeHandler(Handler handler) {
        handlers.remove(handler);
    }
}
