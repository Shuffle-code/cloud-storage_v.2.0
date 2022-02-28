package org.example.command;

import lombok.Data;

@Data
public class DeleteMassage implements CloudMessage {

    private final String fileName;

    public DeleteMassage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE;
    }
}
