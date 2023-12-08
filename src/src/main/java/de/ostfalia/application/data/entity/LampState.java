package de.ostfalia.application.data.entity;

import java.awt.*;

public class LampState {

    private Color color;
    private float intensity;
    private boolean isOn;

    public LampState() {

    }

    public LampState(Color color, float intensity, boolean isOn) {
        this.color = color;
        this.intensity = intensity;
        this.isOn = isOn;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
