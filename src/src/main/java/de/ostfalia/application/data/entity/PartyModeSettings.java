package de.ostfalia.application.data.entity;
import java.awt.Color;

public class PartyModeSettings {
    private int blinkCount;
    private Color[] colors;
    private int[] intensities;

    public PartyModeSettings(int blinkCount, Color[] colors, int[] intensities) {
        this.blinkCount = blinkCount;
        this.colors = colors;
        this.intensities = intensities;
    }

    // Getter und Setter für blinkCount
    public int getBlinkCount() {
        return blinkCount;
    }

    public void setBlinkCount(int blinkCount) {
        this.blinkCount = blinkCount;
    }

    // Getter und Setter für colors
    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    // Getter und Setter für intensities
    public int[] getIntensities() {
        return intensities;
    }

    public void setIntensities(int[] intensities) {
        this.intensities = intensities;
    }
}

