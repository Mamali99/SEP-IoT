package de.ostfalia.application.data.lamp.lampController;

import de.ostfalia.application.data.lamp.adapter.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.lamp.model.AbstractLampController;
import de.ostfalia.application.data.lamp.model.ILamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;

@Service
public class LampController extends AbstractLampController {


    @Autowired
    public LampController(ILamp ilamp) {
        super(ilamp);
    }

    @Override
    public void switchOn() throws IOException {
        iLamp.switchOn();
    }

    @Override
    public void switchOn(float intensity) throws IOException {
        iLamp.switchOn(intensity);

    }

    @Override
    public void switchOn(Color color) throws IOException {
        iLamp.switchOn(color);

    }

    @Override
    public void switchOff() throws IOException {
        iLamp.switchOff();
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
        return iLamp.getState();
    }

    public String getName() throws IOException {
        return iLamp.getName();
    }

    public void setName(String name) throws IOException {
        iLamp.setName(name);
    }
}
