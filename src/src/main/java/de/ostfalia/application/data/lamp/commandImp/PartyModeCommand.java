package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.awt.*;
import java.io.IOException;

public class PartyModeCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private Color[] colors;
    private int[] intensities;

    public PartyModeCommand(Java2NodeRedLampAdapter lamp, int blinkCount, Color[] colors, int[] intensities) {
        this.lamp = lamp;
        this.blinkCount = blinkCount;
        this.colors = colors;
        this.intensities = intensities;
    }
    @Override
    public void execute() throws IOException {
        for (int i = 0; i < blinkCount; i++) {
            // Wechsel zwischen Farben und Intensitäten
            lamp.setColor(colors[i % colors.length]);
            lamp.setIntensity(intensities[i % intensities.length]);
            try {
                Thread.sleep(500); // Wartezeit zwischen den Blinken
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lamp.switchOff();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
