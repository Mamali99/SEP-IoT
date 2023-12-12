package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class TurnOnCommand implements Command {

    private Java2NodeRedLampAdapter lamp;
    private LampState previousState;

    public TurnOnCommand(Java2NodeRedLampAdapter lamp){
        this.lamp = lamp;
    }
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        this.lamp.switchOn();

    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());

    }
    // Methode zum Rückgängigmachen des Befehls
    @Override
    public void undo() throws IOException {

        if (previousState.isOn()) {
            lamp.switchOn();
        } else {
            lamp.switchOff();
        }
    }
    @Override
    public String toString() {
        return "Turn On Command";
    }
}
