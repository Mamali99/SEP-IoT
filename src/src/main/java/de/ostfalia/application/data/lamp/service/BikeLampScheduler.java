package de.ostfalia.application.data.lamp.service;

import de.ostfalia.application.data.lamp.commandImp.BikeDriveCommand;
import de.ostfalia.application.data.lamp.commandImp.RaceCommand;
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
    private Java2NodeRedLampAdapter lampAdapter;

    // Konfiguriere die Fahrradkanäle und Farben
    private final int bikeChannel1 = 1; // Beispielkanal
    private final int bikeChannel2 = 2; // Beispielkanal
    private final Color colorBike1 = Color.RED;
    private final Color colorBike2 = Color.BLUE;

    @Scheduled(fixedRate = 60_000)
    public void scheduleTaskUsingFixedRate() throws IOException {

        BikeDriveCommand bikeDriveCommand = new BikeDriveCommand(lampAdapter, bikeService, bikeChannel1);
        bikeDriveCommand.execute();


        RaceCommand raceCommand = new RaceCommand(lampAdapter, bikeService, bikeChannel1, bikeChannel2, colorBike1, colorBike2);
        raceCommand.execute();
    }



}
