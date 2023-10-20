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


    Java2NodeRedLampAdapter lampAdapter;
    @Autowired
    public LampController(ILamp ilamp) {
        super(ilamp);
    }

    @Override
    public void switchOn() throws IOException {


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
        return lampAdapter.getState();
    }
}
