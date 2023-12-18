package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.awt.*;
import java.io.IOException;

public class PartyModeCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private int blinkCount;
    private Color[] colors;
    private int[] intensities;
    private LampState previousState;
    private volatile boolean running;
    private Thread partyModeThread;
    private int i = 0;

    public PartyModeCommand(Java2NodeRedLampAdapter lamp, int blinkCount, Color[] colors, int[] intensities) {
        this.lamp = lamp;
        this.blinkCount = Integer.MAX_VALUE;
        this.colors = colors;
        this.intensities = intensities;
        this.running = true;
    }

    @Override
    public void execute() throws IOException {
        saveCurrentState();
        if (partyModeThread == null || !partyModeThread.isAlive()) {
            partyModeThread = new Thread(this::performPartyMode);
            partyModeThread.start();
        } else {
            restartPartyMode();
        }
    }

    private void performPartyMode() {

        while (running && i < blinkCount) {
            final Color color = colors[i % colors.length];
            final int intensity = intensities[i % intensities.length];
            changeLampSettings(color, intensity);
           sleepBlinkDuration();
            i++;
        }
    }

    private void changeLampSettings(Color color, int intensity) {
        try {
            lamp.switchOn(color, intensity);
            System.out.println("Party mode " + i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sleepBlinkDuration() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    public void stopPartyMode() {
        running = false;
    }

    private void restartPartyMode() {
        stopPartyMode();
        running = true;
        partyModeThread = new Thread(this::performPartyMode);
        partyModeThread.start();
    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {

        lamp.setColor(this.previousState.getColor());
        lamp.setIntensity(this.previousState.getIntensity());
        if (this.previousState.isOn()) {
            lamp.switchOn();
        } else {
            lamp.switchOff();
        }
    }

    @Override
    public String toString() {
        return "Party Mode";
    }
}





