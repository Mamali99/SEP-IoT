package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.ILamp;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

@Component
@Primary
public class Java2NodeRedLampAdapter implements ILamp {

    private static final String url = "http://172.28.24.10/hue/lights";
    private static final String urlState = "http://172.28.24.10/hue/lights/1";

    // lock object for synchronization
    private final Object lock = new Object();


    @Override
    public void switchOn() throws IOException {

        //Mann kann mit JsonObject jsonObject = Json.createObjectBuilder() request an Hue Bridge schicken
    }

    @Override
    public void switchOn(float intensity) throws IOException {

    }

    @Override
    public void switchOn(Color color) throws IOException {

    }

    @Override
    public void switchOff() throws IOException {

    }

    @Override
    public void setColor(Color color) throws IOException {

    }

    @Override
    public void setIntensity(float intensity) throws IOException {

    }

    @Override
    public Color getColor() throws IOException {
        return null;
    }

    @Override
    public float getIntensity() throws IOException {
        return 0;
    }

    @Override
    public boolean getState() throws IOException {
        return false;
    }
}
