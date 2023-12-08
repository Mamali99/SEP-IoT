package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.awt.*;
import java.io.IOException;

public class SetColorCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private Color color;
    private LampState previousState;

    public SetColorCommand(Java2NodeRedLampAdapter lamp, Color color){
        this.lamp = lamp;
        this.color = color;
    }
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        this.lamp.setColor(color);
    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());

    }

    @Override
    public void undo() throws IOException {

        // Setze den Zustand der Lampe auf den vorher gespeicherten Zustand zurück
        lamp.setColor(previousState.getColor());
        /*lamp.setIntensity(previousState.getIntensity());
        if (previousState.isOn()) {
            lamp.switchOn();
        } else {
            lamp.switchOff();
        }

         */
    }

    //Es muss alte Zustand angezeigt werden
    @Override
    public String toString() {
        String colorHex = String.format("#%02X%02X%02X", previousState.getColor().getRed(), previousState.getColor().getGreen(), previousState.getColor().getBlue());
        return "Set Color Command [Farbe: " + colorHex + "]";
    }

}
