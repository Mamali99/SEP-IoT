package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.Command;

import java.io.IOException;

public class TurnOnCommand implements Command {

    private Java2NodeRedLampAdapter lamp;

    public TurnOnCommand(Java2NodeRedLampAdapter lamp){
        this.lamp = lamp;
    }
    @Override
    public void execute() throws IOException {

        this.lamp.switchOn();
    }
}
