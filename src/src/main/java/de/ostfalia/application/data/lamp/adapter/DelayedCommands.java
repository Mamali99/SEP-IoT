package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.Command;

import java.io.IOException;

public class DelayedCommands implements Command {

    private Java2NodeRedLampAdapter lamp;
    private Command[] commands;
    private long delay;

    public DelayedCommands(Java2NodeRedLampAdapter lamp, Command[] commands, long delay) {
        this.lamp = lamp;
        this.commands = commands;
        this.delay = delay;
    }
    @Override
    public void execute() throws IOException {

    }
}
