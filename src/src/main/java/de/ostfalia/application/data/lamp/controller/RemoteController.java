package de.ostfalia.application.data.lamp.controller;

import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RemoteController {
    private static final int MAX_COMMAND_HISTORY = 5;
    private Command[] commandHistory = new Command[MAX_COMMAND_HISTORY];


    public void executeCommand(Command command) throws IOException {
        command.execute();
        addCommandToHistory(command);

    }

    private void addCommandToHistory(Command command) {
        // Shift commands in the history to make room for the new command
        System.arraycopy(commandHistory, 0, commandHistory, 1, commandHistory.length - 1);
        // Add new command at the beginning of the array
        commandHistory[0] = command;

    }

    public void undoCommand(int commandIndex) throws IOException {

        if (isValidCommandIndex(commandIndex) && commandHistory[commandIndex] != null) {
            Command commandToUndo = commandHistory[commandIndex];
            commandToUndo.undo();
            shiftCommandsAfterUndo(commandIndex);

        }
    }

    private void shiftCommandsAfterUndo(int removedIndex) {
        for (int i = removedIndex; i < commandHistory.length - 1; i++) {
            commandHistory[i] = commandHistory[i + 1];
        }
        // Set the last command to null after the shift
        commandHistory[commandHistory.length - 1] = null;
    }

    private boolean isValidCommandIndex(int commandIndex) {
        return commandIndex >= 0 && commandIndex < commandHistory.length;
    }

    public List<Command> getLastFiveCommands() {
        List<Command> lastCommands = new ArrayList<>();
        for (Command command : commandHistory) {
            if (command != null) {
                lastCommands.add(command);
            }
        }

        return lastCommands;
    }
}
