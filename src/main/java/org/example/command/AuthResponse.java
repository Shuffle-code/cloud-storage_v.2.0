package org.example.command;

import lombok.Data;

@Data
public class AuthResponse implements CloudMessage{

    private Boolean authResponse;

    public AuthResponse(Boolean authResponse) {
        this.authResponse = authResponse;
    }

    @Override
    public CommandType getType() {
        return CommandType.AUTH_RESPONSE;
    }
}
