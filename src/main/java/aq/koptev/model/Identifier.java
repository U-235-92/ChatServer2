package aq.koptev.model;

import aq.koptev.servecies.authentication.Authenticator;
import aq.koptev.servecies.regitraration.Registrar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Identifier {

    private Authenticator authenticator;
    private Registrar registrar;
    private Handler handler;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean isAuthenticationSuccess = false;

    public Identifier(Handler handler, Socket clientSocket) throws IOException {
        this.handler = handler;
        this.clientSocket = clientSocket;
        authenticator = new Authenticator(handler);
        registrar = new Registrar();
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
    }

    public synchronized void processIdentification() throws IOException {
        while(!isAuthenticationSuccess) {
            String message = inputStream.readUTF();
            String registrationResult = "";
            if(!message.startsWith("#")) {
                System.out.println("Bad command");
                continue;
            }
            Command command = Command.getCommandByValue(message.split("\\s+", 2)[0]);
            String data = message.split("\\s+", 2)[1];
            switch (command) {
                case AUTHENTICATION_CLIENT_COMMAND:
                    isAuthenticationSuccess = authenticator.processAuthentication(data);
                    processAuthenticationResult(isAuthenticationSuccess, data);
                    break;
                case REGISTRATION_CLIENT_COMMAND:
                    registrationResult = registrar.processRegistration(data);
                    processRegistrationResult(registrationResult);
                    break;
            }
        }
    }

    private void processRegistrationResult(String registrationResult) throws IOException {
        if(registrationResult == null) {
            handler.sendMessage(Command.OK_REGISTRATION_CLIENT_COMMAND, "Регистрация успешно завершена");
        } else {
            handler.sendMessage(Command.ERROR_REGISTRATION_CLIENT_COMMAND, registrationResult);
        }
    }

    private void processAuthenticationResult(boolean authenticationSuccessState, String data) throws IOException {
        if(authenticationSuccessState) {
            User user = authenticator.getAuthenticatedUser(data);
            String userInfo = String.format("%s %s", user.getLogin(), user.getPassword());
            handler.setUser(user);
            handler.registrationHandler();
            handler.sendMessage(Command.OK_AUTHENTICATION_CLIENT_COMMAND, userInfo);
        } else {
            String errorMessage = authenticator.getErrorMessage(data);
            handler.sendMessage(Command.ERROR_AUTHENTICATION_CLIENT_COMMAND, errorMessage);
        }
    }
}
