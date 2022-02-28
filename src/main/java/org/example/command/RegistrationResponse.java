package org.example.command;

import lombok.Data;

@Data
public class RegistrationResponse implements CloudMessage{

    private Boolean regResponse;

    public RegistrationResponse(Boolean regResponse) {
        this.regResponse = regResponse;
    }

    @Override
    public CommandType getType() {
        return CommandType.REG_RESPONSE;
    }
}
