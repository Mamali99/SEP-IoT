package de.ostfalia.application.data.lamp.commandImp;

import com.vaadin.flow.component.UI;
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

    private volatile boolean running;
    private Thread blinkThread;
    private final UI ui;


    public BlinkCommand(Java2NodeRedLampAdapter lamp, int blinkCount, long blinkDuration) {
        this.lamp = lamp;
        this.blinkCount = Integer.MAX_VALUE;
        this.blinkDuration = 5000;
        this.ui = UI.getCurrent();
        this.running = true;
    }

    @Override
    public void execute() throws IOException {
        saveCurrentState();
        if (blinkThread == null || !blinkThread.isAlive()) {
            blinkThread = new Thread(this::performBlinking);
            blinkThread.start();
        } else {
            restartBlinking();
        }
    }

    private void performBlinking() {
        for (int i = 0; i < blinkCount && running; i++) {
            blinkLamp(true); // Blinken ein
            sleepBlinkDuration();
            blinkLamp(false); // Blinken aus
            sleepBlinkDuration();
        }
    }

    private void blinkLamp(boolean on) {

        ui.access(() -> {
            try {
                if (on) {
                    lamp.switchOn();

                    System.out.println("Lampe is On...");
                } else {
                    System.out.println("Lampe is Off...");
                    lamp.switchOff();

                }
                //lamp.notifyObservers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

         /*
        try {
            if (on) {
                lamp.switchOn();

                System.out.println("Lampe is On...");
            } else {
                System.out.println("Lampe is Off...");
                lamp.switchOff();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

          */
    }

    private void sleepBlinkDuration() {
        try {
            Thread.sleep(blinkDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    public void stopBlinking() {
        running = false;
    }

    private void restartBlinking() {
        stopBlinking();
        running = true;
        blinkThread = new Thread(this::performBlinking);
        blinkThread.start();
    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {
        stopBlinking(); // Stellt sicher, dass das Blinken gestoppt wird
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
        return "Blink";
    }
}
