package de.ostfalia.application.data.lamp.service;

import de.ostfalia.application.data.lamp.commandImp.BikeDriveCommand;
import de.ostfalia.application.data.lamp.commandImp.RaceCommand;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.service.BikeService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.math.BigDecimal;


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
    private BikeDriveCommand bikeDriveCommand;
    private RaceCommand raceCommand;

    private Integer selectedChannel;

    private Integer bikeChannelForBike1;
    private Integer bikeChannelForBike2;
    private volatile boolean schedulerPaused = false; // Add this flag

    public void setBikeChannels(Integer channel1, Integer channel2) {
        this.bikeChannelForBike1 = channel1;
        this.bikeChannelForBike2 = channel2;
    }


    public void setSelectedChannel(Integer selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    @PreDestroy
    public void stopScheduledTasks() {
        this.driveCommandEnabled = false;
        this.raceCommandEnabled = false;
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 0) // alle 60 Sekunden
    public void scheduleTaskUsingFixedRate() {
        try {
            if (schedulerPaused) {
                return; // Exit the method if the scheduler is paused
            }

            if (this.selectedChannel != null && this.driveCommandEnabled) {
                bikeDriveCommand = new BikeDriveCommand(lampAdapter, bikeService, selectedChannel);
                remoteController.executeCommand(bikeDriveCommand);
            }

            if (this.raceCommandEnabled) {
                raceCommand = new RaceCommand(lampAdapter, bikeService, bikeChannelForBike1, bikeChannelForBike2, colorBike1, colorBike2);
                remoteController.executeCommand(raceCommand);
            }
        } catch (Exception e) {
            // Log the stack trace for debugging
            e.printStackTrace();
        }

    }


    public void enableRaceCommand() {
        if(!driveCommandEnabled) {
            this.raceCommandEnabled = true;
        }
    }

    public void disableRaceCommand() {
        System.out.println("Set this Race to false");
        this.raceCommandEnabled = false;
    }

    public void enableDriveCommand() {
        if(!raceCommandEnabled) {
            this.driveCommandEnabled = true;
        }
    }

    public void disableDriveCommand() {
        this.driveCommandEnabled = false;
    }

    public boolean isDriveCommandEnabled() {
        return driveCommandEnabled;
    }

    public boolean isRaceCommandEnabled() {
        return raceCommandEnabled;
    }

    public BigDecimal getBikeDriveSpeed() {
        return bikeDriveCommand.getBikeSpeed();
    }

    public int getBikeRaceWinnerInt() {
        return raceCommand.getWinningChannel();
    }

    public Color getBikeRaceWinnerColor() {
        return raceCommand.getWinningColor();
    }

    // Method to pause the scheduler
    public void pauseScheduler() {
        this.schedulerPaused = true;
        System.out.println("Scheduler is on Pause");
    }

    // Method to resume the scheduler
    public void resumeScheduler() {
        this.schedulerPaused = false;
        System.out.println("Scheduler is Resumed");
    }

}
