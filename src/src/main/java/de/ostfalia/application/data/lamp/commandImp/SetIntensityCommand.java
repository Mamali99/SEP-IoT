package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class SetIntensityCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private float intensity;
    private LampState previousState;

    public SetIntensityCommand(Java2NodeRedLampAdapter lamp, float intensity){
        this.lamp = lamp;
        this.intensity = intensity;
    }
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        this.lamp.setIntensity(intensity);

    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {
        lamp.setIntensity(previousState.getIntensity());

    }
    @Override
    public String toString() {
        return "Intensity";
    }
}
