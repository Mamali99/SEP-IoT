package de.ostfalia.application.data.lamp.service;

import de.ostfalia.application.data.lamp.commandImp.BikeDriveCommand;
import de.ostfalia.application.data.lamp.commandImp.RaceCommand;
import de.ostfalia.application.data.lamp.controller.RemoteController;
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
    @Autowired
    private RemoteController remoteController;
    private volatile boolean raceCommandEnabled = false;

    private volatile boolean driveCommandEnabled = false;

    private final Color colorBike1 = Color.RED;
    private final Color colorBike2 = Color.BLUE;


    private Integer selectedChannel;

    private Integer bikeChannelForBike1;
    private Integer bikeChannelForBike2;

    public void setBikeChannels(Integer channel1, Integer channel2) {
        this.bikeChannelForBike1 = channel1;
        this.bikeChannelForBike2 = channel2;
    }


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
            RaceCommand raceCommand = new RaceCommand(lampAdapter, bikeService, bikeChannelForBike1, bikeChannelForBike2, colorBike1, colorBike2);
            //raceCommand.execute();
            remoteController.executeCommand(raceCommand);
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

    public boolean isDriveCommandEnabled() {
        return driveCommandEnabled;
    }

    public boolean isRaceCommandEnabled() {
        return raceCommandEnabled;
    }


}
