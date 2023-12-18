package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class BikeDriveCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private BikeService bikeService;

    private int previousIntensity;

    private  boolean bikeDriveCommand = true;

    private static final BigDecimal CIRCUMFERENCE = new BigDecimal("2.111");
    private static final BigDecimal ROTATION_DIVISOR = new BigDecimal(4);
    private static final int MAX_INTENSITY = 254;
    private int bikeChannel;

    private BigDecimal bikeSpeed;
    private static final BigDecimal MAX_SPEED = BigDecimal.valueOf(50); // Maximalgeschwindigkeit laut pdf

    public BikeDriveCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService, int bikeChannel) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel = bikeChannel;
    }
    @Override
    public void execute() throws IOException {
        bikeDriveCommand = true;
        previousIntensity = (int) lamp.getIntensity();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);

        List<Bicycle> bikeData = bikeService.getDataWithTimeSpan(bikeChannel, oneMinuteAgo, now);
        if (bikeData.isEmpty()) {
            System.out.println("Keine Fahrraddaten gefunden für Kanal: " + bikeChannel);
        } else {
            System.out.println("Ja die daten sind da");
        }
        BigDecimal speedInKmph = calculateSpeed(bikeData); // speed in kmh
        System.out.println("Speed in Kmph " + speedInKmph);
        BigDecimal ratio = speedInKmph.divide(MAX_SPEED, 2, RoundingMode.HALF_UP);
        System.out.println("Ratio " + ratio);
        BigDecimal calculatedIntensity = ratio.multiply(BigDecimal.valueOf(MAX_INTENSITY));
        System.out.println("Calculated Intensity " + calculatedIntensity);

        if (calculatedIntensity.compareTo(BigDecimal.valueOf(MAX_INTENSITY)) > 0) {
            calculatedIntensity = BigDecimal.valueOf(MAX_INTENSITY);
        }
        lamp.setIntensity(calculatedIntensity.intValue());
        System.out.println("Before setting intensity on lamp " + calculatedIntensity.intValue());
        lamp.setIntensity(calculatedIntensity.intValue());

    }

    @Override
    public void saveCurrentState() {

    }

    @Override
    public void undo() throws IOException {
        bikeDriveCommand = false;
        lamp.setIntensity(previousIntensity);
    }


    private BigDecimal calculateSpeed(List<Bicycle> bikeData) {
        if (bikeData.isEmpty()) {
            System.out.println("bikedata is empty");
            return BigDecimal.ZERO;
        }
        BigDecimal totalSpeed = BigDecimal.ZERO;
        int bikeDataCount = bikeData.size();

        for (Bicycle bike : bikeData) {
            totalSpeed = totalSpeed.add(calculateSpeedPerBike(bike));
        }

        bikeSpeed = totalSpeed.divide(BigDecimal.valueOf(bikeDataCount), BigDecimal.ROUND_HALF_UP);

        return bikeSpeed;
    }

    public BigDecimal calculateSpeedPerBike(Bicycle bike) {
        // zunächst in m/s
        BigDecimal speedInMps = bike.getRotations().divide(ROTATION_DIVISOR, 2, RoundingMode.HALF_UP).multiply(CIRCUMFERENCE);
        // Umwandlung von m/s in km/h
        BigDecimal speedInKmph = speedInMps.multiply(BigDecimal.valueOf(3.6));
        return speedInKmph;
    }

    public String toString() {
        return "Bike Drive";
    }

    public BigDecimal getBikeSpeed() {
        return bikeSpeed;
    }
}
