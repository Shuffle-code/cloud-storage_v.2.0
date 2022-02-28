package org.example.command;

import lombok.Data;

@Data
public class CreateMassage implements CloudMessage {

    private final String DirName;

    public CreateMassage(String DirName) {
        this.DirName = DirName;
    }

    public String getFileName() {
        return DirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.CREATE;
    }
}
