package de.ostfalia.application.data.lamp.commandImp;


import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.entity.LampState;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;



public class RaceCommand implements Command {



    private Java2NodeRedLampAdapter lamp;
    private BikeService bikeService;
    private int bikeChannel1;
    private int bikeChannel2;
    private Color colorBike1;
    private Color colorBike2;
    private Duration duration;
    private static final BigDecimal CIRCUMFERENCE = new BigDecimal("2.111");
    private static final int ROTATION_DIVISOR = 4;

    private LampState previousState;

    private BigDecimal distanceBike1;
    private BigDecimal distanceBike2;
    private int winningChannel;
    private Color winningColor;




    public RaceCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService,
                       int bikeChannel1, int bikeChannel2,
                       Color colorBike1, Color colorBike2) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel1 = bikeChannel1;
        this.bikeChannel2 = bikeChannel2;
        this.colorBike1 = colorBike1;
        this.colorBike2 = colorBike2;
        this.duration = Duration.ofMinutes(1);
        try {
            saveCurrentState();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void execute() throws IOException {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(duration);

                distanceBike1 = calculateTotalDistance(bikeService.getDataWithTimeSpan(bikeChannel1, startTime, endTime));
                distanceBike2 = calculateTotalDistance(bikeService.getDataWithTimeSpan(bikeChannel2, startTime, endTime));
                winningChannel = distanceBike1.compareTo(distanceBike2) >= 0 ? bikeChannel1 : bikeChannel2;
                winningColor = distanceBike1.compareTo(distanceBike2) >= 0 ? colorBike1 : colorBike2;


                float intensity = calculateIntensity(distanceBike1, distanceBike2);
                try {
                    lamp.switchOn(winningColor, intensity);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

    }

    public String getRaceSummary() {
        String summary = "Race Summary:\n";
        summary += "Distance Bike 1: " + distanceBike1 + " units\n";
        summary += "Distance Bike 2: " + distanceBike2 + " units\n";
        summary += "Winning Bike: Channel " + winningChannel + "\n";
        summary += "Winning Color: " + winningColor + "\n";
        return summary;
    }

    @Override
    public void saveCurrentState() throws IOException {
        previousState = new LampState(lamp.getColor(), lamp.getIntensity(), lamp.getState());
    }

    @Override
    public void undo() throws IOException {

        if (previousState != null) {
            lamp.setColor(previousState.getColor());
            lamp.setIntensity(previousState.getIntensity());
            if (previousState.isOn()) {
                lamp.switchOn();
            } else {
                lamp.switchOff();
            }
        }
    }

    private BigDecimal calculateTotalDistance(List<Bicycle> bikeData) {
        if (bikeData == null || bikeData.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDistance = BigDecimal.ZERO;

        for (Bicycle bike : bikeData) {
            totalDistance = totalDistance.add(calculateDistancePerEntry(bike));
        }

        return totalDistance;
    }

    private BigDecimal calculateDistancePerEntry(Bicycle bike) {
        BigDecimal realRotationsPerSecond = bike.getRotations()
                .divide(new BigDecimal(ROTATION_DIVISOR), 2, RoundingMode.HALF_UP);
        return realRotationsPerSecond.multiply(CIRCUMFERENCE);
    }

    private float calculateIntensity(BigDecimal distance1, BigDecimal distance2) {
        BigDecimal difference = distance1.subtract(distance2).abs();
        // Maximaler Intensitätswert, z.B. 254
        float maxIntensity = 254;
        return Math.min(maxIntensity, difference.floatValue());
    }

    @Override
    public String toString(){
        return "Race Mode";
    }

}
