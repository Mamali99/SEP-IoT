package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class TurnOffCommand implements Command {
    private Java2NodeRedLampAdapter lamp;

    public TurnOffCommand(Java2NodeRedLampAdapter lamp){
        this.lamp = lamp;
    }
    @Override
    public void execute() throws IOException {
        this.lamp.switchOff();
        System.out.println("Lampe ist Off....");
    }
}
