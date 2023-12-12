package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.BlinkSettings;
import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.awt.*;
import java.io.IOException;

public class BlinkCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private long blinkDuration;

    private LampState previousState;

    //hier muss ich vieleicht der alte Zusatand von der Lampe speichern
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
        previousState = new LampState();
       previousState.setColor(lamp.getColor());
       previousState.setIntensity(lamp.getIntensity());


    }

    @Override
    public void undo() throws IOException {

        lamp.setColor(this.previousState.getColor());
        lamp.setIntensity(this.previousState.getIntensity());

    }
    @Override
    public String toString() {
        return "Blink Command [Anzahl: " + blinkCount + ", Dauer: " + blinkDuration + "ms]";
    }
}
