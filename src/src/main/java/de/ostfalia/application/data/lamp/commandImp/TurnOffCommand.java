package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class TurnOffCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private LampState previousState;

    public TurnOffCommand(Java2NodeRedLampAdapter lamp){
        this.lamp = lamp;
    }
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        this.lamp.switchOff();
        System.out.println("Lampe ist Off....");
    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {

        if (previousState.isOn()) {
            lamp.switchOn();
        } else {
            lamp.switchOff();
        }
        lamp.notifyObservers();

    }
    @Override
    public String toString() {
        return "Turn Off";
    }

}
