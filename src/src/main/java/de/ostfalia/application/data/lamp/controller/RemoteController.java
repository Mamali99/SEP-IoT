package de.ostfalia.application.data.lamp.controller;

import de.ostfalia.application.data.lamp.model.Command;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class RemoteController {
    private static final int MAX_COMMAND_HISTORY = 5;
    private List<Command> commandHistory = new ArrayList<>();

    public void executeCommand(Command command) throws IOException {
        if (commandHistory.size() >= MAX_COMMAND_HISTORY) {
            commandHistory.remove(0); // Entferne den ältesten Befehl, wenn das Limit erreicht ist
        }
        command.execute();
        commandHistory.add(command);
    }

    public void undoCommand(int commandIndex) throws IOException {
        if (commandIndex >= 0 && commandIndex < commandHistory.size()) {
            Command commandToUndo = commandHistory.get(commandIndex);
            commandToUndo.undo();
            commandHistory.remove(commandIndex); // Entferne den rückgängig gemachten Befehl aus der Historie
        }
    }

    // Eine Methode, um die letzten 5 Befehle abzurufen
    public List<Command> getLastFiveCommands() {
        return new ArrayList<>(commandHistory); // Gibt eine Kopie der Historie zurück
    }
}

