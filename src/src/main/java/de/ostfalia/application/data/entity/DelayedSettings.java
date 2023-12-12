package de.ostfalia.application.data.entity;

import de.ostfalia.application.data.lamp.model.Command;

public class DelayedSettings {

    private Command[] commands;
    private long delay;



    public Command[] getCommands() {
        return commands;
    }

    public void setCommands(Command[] commands) {
        this.commands = commands;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
