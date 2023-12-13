package de.ostfalia.application.data.lamp.commandImp;

import com.vaadin.flow.component.UI;
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

    private LampState lampState;




    public RaceCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService,
                       int bikeChannel1, int bikeChannel2,
                       Color colorBike1, Color colorBike2) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel1 = bikeChannel1;
        this.bikeChannel2 = bikeChannel2;
        this.colorBike1 = colorBike1;
        this.colorBike2 = colorBike2;
        this.duration = Duration.ofMinutes(5);


    }

    @Override
    public void execute() throws IOException {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(duration);

                BigDecimal distanceBike1 = calculateTotalDistance(bikeService.getDataWithTimeSpan(bikeChannel1, startTime, endTime));
                System.out.println("Distance of Bike: " + bikeChannel1 + " is: " + distanceBike1);
                BigDecimal distanceBike2 = calculateTotalDistance(bikeService.getDataWithTimeSpan(bikeChannel2, startTime, endTime));
                System.out.println("Distance of Bike: " + bikeChannel2 + " is: " + distanceBike2);
                Color winningColor = distanceBike1.compareTo(distanceBike2) >= 0 ? colorBike1 : colorBike2;
                System.out.println("The winning color: " + winningColor.getRed());
                System.out.println(distanceBike1.compareTo(distanceBike2) >= 0 ? bikeChannel1 : bikeChannel2);

                float intensity = calculateIntensity(distanceBike1, distanceBike2);
                System.out.println("Intensität: " + intensity);

                lampState = new LampState(winningColor, intensity, true);
                System.out.println(lampState.toString());



                /*
                try {
                    lamp.setColor(winningColor);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                 */


    }

    @Override
    public void saveCurrentState() {

    }

    @Override
    public void undo() throws IOException {

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

}
