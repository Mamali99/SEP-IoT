package de.ostfalia.application.data.lamp.service;

import com.vaadin.flow.component.UI;
import de.ostfalia.application.data.lamp.commandImp.BikeDriveCommand;
import de.ostfalia.application.data.lamp.commandImp.RaceCommand;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



import java.awt.*;
import java.io.IOException;


@Component
public class BikeLampScheduler {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private Java2NodeRedLampAdapter lampAdapter; //es muss genau gleiches lampAdapter hier geben, wie LampView hat und andere Command krigen
    @Autowired
    private RemoteController remoteController;
    private volatile boolean raceCommandEnabled = false;

    private volatile boolean driveCommandEnabled = false;

    // Konfiguriere die Fahrradkanäle und Farben
    private final int bikeChannel1 = 1; // Beispielkanal
    private final int bikeChannel2 = 2; // Beispielkanal
    private final Color colorBike1 = Color.RED;
    private Command currentCommand;
    private final Color colorBike2 = Color.BLUE;

    // In der BikeLampScheduler Klasse
    private Integer selectedChannel;



    public void setSelectedChannel(Integer selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    @Scheduled(fixedRate = 10_000, initialDelay = 0) // alle 60 Sekunden
    public void scheduleTaskUsingFixedRate() throws IOException {

        if (this.selectedChannel != null && this.driveCommandEnabled) {
            BikeDriveCommand currentCommand = new BikeDriveCommand(lampAdapter, bikeService, selectedChannel);
            remoteController.executeCommand(currentCommand);
        }

        if (this.raceCommandEnabled) {
            RaceCommand raceCommand = new RaceCommand(lampAdapter, bikeService, bikeChannel1, bikeChannel2, colorBike1, colorBike2);
            //remoteController.executeCommand(raceCommand);
            raceCommand.execute();
        }

    }

    public void enableRaceCommand() {
        this.raceCommandEnabled = true;
    }

    public void disableRaceCommand() {
        this.raceCommandEnabled = false;
    }

    public void enableDriveCommand() {
        this.driveCommandEnabled = true;
    }

    public void disableDriveCommand() {
        System.out.println("Set this Drive to false");
        this.driveCommandEnabled = false;
    }



}
