package org.example.command;

import lombok.Data;

@Data
public class ChangePath implements CloudMessage {

    private final String dirName;

    public ChangePath (String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_REQUEST;
    }
}
