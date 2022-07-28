package aq.koptev.model;

public enum Command {


    AUTHENTICATION_CLIENT_COMMAND("#authentication"),
    REGISTRATION_CLIENT_COMMAND("#registration"),
    ERROR_AUTHENTICATION_CLIENT_COMMAND("#err_autentication"),
    ERROR_REGISTRATION_CLIENT_COMMAND("#err_registration"),
    OK_AUTHENTICATION_CLIENT_COMMAND("#ok_authentication"),
    OK_REGISTRATION_CLIENT_COMMAND("#ok_registration"),

    COMMON_MESSAGE_COMMAND("#common_msg"),
    PRIVATE_MESSAGE_COMMAND("#private_message"),

    NOTIFY_CLIENTS_ON_CLIENT_CONNECT_COMMAND("#notify_clients_on_client_connect"),
    CLIENT_CONNECT_COMMAND("#client_connected"),
    CLIENT_DISCONNECT_COMMAND("#client_disconected"),

    REQUEST_CONNECTED_CLIENTS_COMMAND("#request_connected_clients"),
    REQUEST_CONNECTED_CLIENT_COMMAND("#request_connected_client"),
    ANSWER_CONNECTED_CLIENTS_COMMAND("#answer_connected_clients"),
    ANSWER_CONNECTED_CLIENT_COMMAND("#answer_connected_client");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Command getCommandByValue(String command) {
        for(Command c : Command.values()) {
            if(c.getCommand().equals(command)) {
                return c;
            }
        }
        return null;
    }
}
