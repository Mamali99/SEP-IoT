package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.lamp.model.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomCommand implements Command {

    private List<Command> commandList;

    public CustomCommand() {
        this.commandList = new ArrayList<>();
    }
    @Override
    public void execute() throws IOException {
        for (Command command : commandList) {
            command.execute();
        }
    }

    public void addCommand(Command command) {
        commandList.add(command);
    }

    @Override
    public void saveCurrentState() throws IOException {
        for (Command command : commandList) {
            command.saveCurrentState();
        }
    }

    @Override
    public void undo() throws IOException {
        for (int i = commandList.size() - 1; i >= 0; i--) {
            commandList.get(i).undo();
        }
    }
}
