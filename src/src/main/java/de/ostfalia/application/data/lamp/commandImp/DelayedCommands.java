package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;

import java.io.IOException;

public class DelayedCommands implements Command {

    private Java2NodeRedLampAdapter lamp;
    private Command[] commands;
    private long delay;

    private LampState previousState;


    public DelayedCommands(Java2NodeRedLampAdapter lamp, Command[] commands, long delay) {
        this.lamp = lamp;
        this.commands = commands;
        this.delay = delay;
    }

    //Nach eine Zeitverzögerung werden alle Commands hintereinander ausgeführt.
    @Override
    public void execute() throws IOException {
        saveCurrentState();
        try {

            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        for (Command command : commands) {
            command.execute();
        }

    }

    @Override
    public void saveCurrentState() throws IOException {
       previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());

    }

    @Override
    public void undo() throws IOException {
        lamp.setColor(this.previousState.getColor());
        lamp.setIntensity(this.previousState.getIntensity());
        if(this.previousState.isOn()){
            lamp.switchOn();
        }else{
            lamp.switchOff();
        }


    }
}
