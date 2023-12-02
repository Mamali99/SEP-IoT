package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class SetIntensityCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private float intensity;

    public SetIntensityCommand(Java2NodeRedLampAdapter lamp, float intensity){
        this.lamp = lamp;
        this.intensity = intensity;
    }
    @Override
    public void execute() throws IOException {
        this.lamp.setIntensity(intensity);

    }
}
