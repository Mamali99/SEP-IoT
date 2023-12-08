package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class BlinkCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private long blinkDuration;

    public BlinkCommand(Java2NodeRedLampAdapter lamp, int blinkCount, long blinkDuration) {
        this.lamp = lamp;
        this.blinkCount = blinkCount;
        this.blinkDuration = blinkDuration;
    }

    @Override
    public void execute() throws IOException {
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
}
