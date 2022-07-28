package aq.koptev;

import aq.koptev.model.Server;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.waitConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
