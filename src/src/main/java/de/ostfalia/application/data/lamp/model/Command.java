package de.ostfalia.application.data.lamp.model;

import java.io.IOException;

public interface Command {

    void execute() throws IOException;
    void saveCurrentState() throws IOException;
    void undo() throws IOException;

}
