package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class BlinkCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private long blinkDuration;
    private LampState previousState;

    public BlinkCommand(Java2NodeRedLampAdapter lamp, int blinkCount, long blinkDuration) {
        this.lamp = lamp;
        this.blinkCount = blinkCount;
        this.blinkDuration = blinkDuration;
    }

    @Override
    public void execute() throws IOException {
        saveCurrentState();
        for (int i = 0; i < blinkCount; i++) {
            lamp.switchOn(); // Lampe einschalten
            try {
                System.out.println("Lampe ist On.");
                Thread.sleep(blinkDuration); // Warte für die Dauer des Blinkens
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lamp.switchOff(); // Lampe ausschalten
            try {
                System.out.println("Lampe ist Off");
                Thread.sleep(blinkDuration); // Warte für die Dauer des Blinkens
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {
// Setze den Zustand der Lampe auf den vorher gespeicherten Zustand zurück
        lamp.setColor(previousState.getColor());
        lamp.setIntensity(previousState.getIntensity());
        if (previousState.isOn()) {
            lamp.switchOn();
        } else {
            lamp.switchOff();
        }
    }
    @Override
    public String toString() {
        return "Blink Command [Anzahl: " + blinkCount + ", Dauer: " + blinkDuration + "ms]";
    }
}
