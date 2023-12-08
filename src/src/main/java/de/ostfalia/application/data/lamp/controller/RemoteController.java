package de.ostfalia.application.data.lamp.controller;

import de.ostfalia.application.data.lamp.model.Command;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Stack;

@Component
public class RemoteController {

    private Stack<Command> commandStack = new Stack<>();


    public void executeCommand(Command command) throws IOException {
        command.execute();
        commandStack.push(command);
    }

    public void undoLastCommand(){
        if(!commandStack.isEmpty()){
            Command lastCommand = commandStack.pop();

            // Logik implementieren
        }
    }


}
