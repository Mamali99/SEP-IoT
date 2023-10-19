package de.ostfalia.application.data.lamp.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.IOException;

public abstract class AbstractLampController {


    public ILamp iLamp;

    @Autowired
    public AbstractLampController(ILamp ilamp){
        this.iLamp = ilamp;
    }


    public abstract void switchOn() throws IOException;


    public abstract void switchOn(float intensity) throws IOException;


    public abstract void switchOn(Color color) throws IOException;


    public abstract void switchOff() throws IOException;


    public abstract void setColor(Color color) throws IOException;


    public abstract void setIntensity(float intensity) throws IOException;


    public abstract Color getColor() throws IOException;


    public abstract float getIntensity() throws IOException;


    public abstract boolean getState() throws IOException;


}

