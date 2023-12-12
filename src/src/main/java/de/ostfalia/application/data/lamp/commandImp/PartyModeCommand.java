package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.entity.PartyModeSettings;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.awt.*;
import java.io.IOException;

public class PartyModeCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private Color[] colors;
    private int[] intensities;

    private PartyModeSettings previousState;





    public PartyModeCommand(Java2NodeRedLampAdapter lamp, int blinkCount, Color[] colors, int[] intensities) {
        this.lamp = lamp;
        this.blinkCount = blinkCount;
        this.colors = colors;
        this.intensities = intensities;
    }
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        lamp.switchOn();
        for (int i = 0; i < blinkCount; i++) {
            // Wechsel zwischen Farben und Intensitäten
            lamp.setColor(colors[i % colors.length]);
            lamp.setIntensity(intensities[i % intensities.length]);

            try {
                Thread.sleep(1000); // Wartezeit zwischen den Blinken
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //lamp.switchOff();
            System.out.println("Lampe ist on...");
            //try {
            //    Thread.sleep(500);
            //} catch (InterruptedException e) {
            //    Thread.currentThread().interrupt();
            //}
        }
    }

    @Override
    public void saveCurrentState() throws IOException {

    }

    @Override
    public void undo() throws IOException {
        // Setze den Zustand der Lampe auf den vorher gespeicherten Zustand zurück
       this.blinkCount = previousState.getBlinkCount();
       this.colors = previousState.getColors();
       this.intensities = previousState.getIntensities();
       lamp.switchOn();
    }
    @Override
    public String toString() {
        return "Party Mode Command [Blinkanzahl: " + blinkCount + "]";
    }
}
