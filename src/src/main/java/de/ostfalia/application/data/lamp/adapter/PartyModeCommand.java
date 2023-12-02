package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.Command;

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

    }
}
