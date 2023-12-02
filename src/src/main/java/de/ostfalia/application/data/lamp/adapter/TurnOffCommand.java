package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.Command;

import java.io.IOException;

public class TurnOffCommand implements Command {
    private Java2NodeRedLampAdapter lamp;

    public TurnOffCommand(Java2NodeRedLampAdapter lamp){
        this.lamp = lamp;
    }
    @Override
    public void execute() throws IOException {

    }
}
