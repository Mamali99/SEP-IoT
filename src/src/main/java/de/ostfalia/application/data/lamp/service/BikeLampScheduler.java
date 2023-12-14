package de.ostfalia.application.data.lamp.service;

import com.vaadin.flow.component.UI;
import de.ostfalia.application.data.lamp.commandImp.BikeDriveCommand;
import de.ostfalia.application.data.lamp.commandImp.RaceCommand;
import de.ostfalia.application.data.service.BikeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;


import java.awt.*;
import java.io.IOException;
import java.time.Duration;

@Component
public class BikeLampScheduler {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private Java2NodeRedLampAdapter lampAdapter; //es muss genau gleiches lampAdapter hier geben, wie LampView hat und andere Command krigen

    private volatile boolean raceCommandEnabled = false;

    private  boolean driveCommandEnabled = false;

    // Konfiguriere die Fahrradkanäle und Farben
    private final int bikeChannel1 = 1; // Beispielkanal
    private final int bikeChannel2 = 2; // Beispielkanal
    private final Color colorBike1 = Color.RED;
    private final Color colorBike2 = Color.BLUE;

    // In der BikeLampScheduler Klasse
    private Integer selectedChannel;


    public void setSelectedChannel(Integer selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    @Scheduled(fixedRate = 60_000) // alle 60 Sekunden
    public void scheduleTaskUsingFixedRate() throws IOException {

        if (this.selectedChannel != null) {
            if (this.driveCommandEnabled) {
                BikeDriveCommand bikeDriveCommand = new BikeDriveCommand(lampAdapter, bikeService, selectedChannel);
                bikeDriveCommand.execute();
            }
        }
        if (this.raceCommandEnabled) {
            RaceCommand raceCommand = new RaceCommand(lampAdapter, bikeService, bikeChannel1, bikeChannel2, colorBike1, colorBike2);
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
