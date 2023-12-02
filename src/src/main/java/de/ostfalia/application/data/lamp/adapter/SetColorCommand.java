package de.ostfalia.application.data.lamp.adapter;

import de.ostfalia.application.data.lamp.model.Command;

import java.awt.*;
import java.io.IOException;

public class SetColorCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private Color color;

    public SetColorCommand(Java2NodeRedLampAdapter lamp, Color color){
        this.lamp = lamp;
        this.color = color;
    }
    @Override
    public void execute() throws IOException {
        this.lamp.setColor(color);
    }
}
