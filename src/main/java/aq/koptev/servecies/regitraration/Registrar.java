package aq.koptev.servecies.regitraration;

import aq.koptev.model.User;

import java.io.IOException;

public class Registrar {

    private RegistrationService registrationService;

    public Registrar() {
        registrationService = new DBRegistrationService();
    }

    public String processRegistration(String data) {
        String login = data.split("\\s+", 2)[0];
        String password = data.split("\\s+", 2)[1];
        if(login.matches("\\s+")) {
            return "Логин не может содержать символ пробела";
        } else if(login.length() > 30 || password.length() > 30) {
            return "Поле логин и пароль не могут быть длинее 30 символов";
        } else {
            User user = new User(login, password);
            if(registrationService.registerUser(user)) {
                return null;
            } else {
                return String.format("Логин %s уже занят", login);
            }
        }
    }
}
