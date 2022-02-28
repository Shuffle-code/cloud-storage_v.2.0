package org.example.command;

import lombok.Data;

@Data
public class RegistrationMessage implements CloudMessage{
    private String name;
    private String login;
    private String password;

    public RegistrationMessage(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION;
    }
}
