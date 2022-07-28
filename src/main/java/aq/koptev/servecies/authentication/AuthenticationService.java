package aq.koptev.servecies.authentication;

import aq.koptev.model.User;

public interface AuthenticationService {

    User getUser(String login, String password);
    boolean isExistUser(String login);
    String getErrorAuthenticationMessage(String login, String password);
}
