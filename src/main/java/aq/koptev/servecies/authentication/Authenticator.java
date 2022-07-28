package aq.koptev.servecies.authentication;

import aq.koptev.model.Handler;
import aq.koptev.model.User;

import java.io.IOException;

public class Authenticator {
    private AuthenticationService authenticationService;
    private Handler handler;

    public Authenticator(Handler handler) {
        this.handler = handler;
        authenticationService = new DBAuthenticationService();
    }
    public boolean processAuthentication(String data) throws IOException {
        String[] parts = data.split("\\s+");
        if(parts == null || parts.length > 2 || parts.length == 0) {
            return false;
        } else if(parts.length == 1) {
            String login = parts[0];
            if(isAuthenticationSuccess(login, "")) {
                return true;
            }
        } else {
            String login = parts[0];
            String password = parts[1];
            if(isAuthenticationSuccess(login, password)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthenticationSuccess(String login, String password) {
        User user = authenticationService.getUser(login, password);
        if(user == null) {
            return false;
        } else {
            if(handler.isUserConnected(login)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public User getAuthenticatedUser(String data) {
        String[] parts = data.split("\\s+");
        if(parts == null || parts.length > 2 || parts.length == 0) {
            return null;
        } else if(parts.length == 1) {
            String login = parts[0];
            if(isAuthenticationSuccess(login, "")) {
                return new User(login, "");
            }
        } else {
            String login = parts[0];
            String password = parts[1];
            if(isAuthenticationSuccess(login, password)) {
                return new User(login, password);
            }
        }
        return null;
    }

    public String getErrorMessage(String message) {
        String[] parts = message.split("\\s+");
        if(parts == null || parts.length > 2 || parts.length == 0) {
            return "Логин не может содержать пробел или быть пустым";
        } else if(parts.length == 1) {
            String login = parts[0];
            if(handler.isUserConnected(login)) {
                return String.format("Пользователь с логином %s уже авторизован", login);
            } else {
                return authenticationService.getErrorAuthenticationMessage(login, "");
            }
        } else {
            String login = parts[0];
            String password = parts[1];
            if(handler.isUserConnected(login)) {
                return String.format("Пользователь с логином %s уже авторизован", login);
            } else {
                return authenticationService.getErrorAuthenticationMessage(login, password);
            }
        }
    }
}
