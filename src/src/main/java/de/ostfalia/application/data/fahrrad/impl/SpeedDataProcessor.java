/*
package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    private final String processorName = "Speed";


    @Autowired
    public SpeedDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    // Implementierung für die Standard-Abfrage von Daten zwischen Start- und Endzeit
    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        return bikeService.getDataWithTimeSpan(channel, startTime, endTime);
    }

    // Implementierung für das Abrufen der letzten Aktivität eines Kanals
    @Override
    protected List<Bicycle> fetchLastActivity(int channel) {
        return bikeService.getBicyclesSinceLastActivity(channel);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

        Duration intervalSize;
        if (intervalInSeconds <= 0) {
            intervalSize = determineAutomaticInterval(bicycles);
        } else {
            intervalSize = Duration.ofSeconds(intervalInSeconds);
        }

        List<ProcessedData> speedData = new ArrayList<>();
        LocalDateTime intervalStart = null;
        BigDecimal totalSpeed = BigDecimal.ZERO;
        int count = 0;

        for (Bicycle bike : bicycles) {
            // Neues Intervall starten, wenn nötig
            if (intervalStart == null || bike.getTime().isAfter(intervalStart.plus(intervalSize))) {

                if (intervalStart != null) {
                    BigDecimal averageSpeed = count > 0 ? totalSpeed.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    speedData.add(new ProcessedData(bike.getChannel(), averageSpeed, intervalStart, processorName));
                }
                intervalStart = bike.getTime();
                totalSpeed = BigDecimal.ZERO;
                count = 0;
            }

            //BigDecimal distance = bike.getRotations().multiply(new BigDecimal("2.111"));
            BigDecimal distance = calculateSpeedPerBike(bike);
            totalSpeed = totalSpeed.add(distance);
            count++;
        }

        // Letztes Intervall hinzufügen
        if (count > 0) {
            BigDecimal averageSpeed = totalSpeed.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
            speedData.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), averageSpeed, intervalStart, processorName));
        }
        return speedData;
    }


    private Duration determineAutomaticInterval(List<Bicycle> bicycles) {
        if (bicycles.isEmpty()) {
            return Duration.ofMinutes(1); // Default interval if no data is available
        }

        LocalDateTime earliestTimestamp = bicycles.get(0).getTime();
        LocalDateTime latestTimestamp = bicycles.get(bicycles.size() - 1).getTime();
        long dataTimespanInSeconds = Duration.between(earliestTimestamp, latestTimestamp).getSeconds();
        long targetIntervalInSeconds = dataTimespanInSeconds / 15;

        if (targetIntervalInSeconds <= 60) {
            return Duration.ofSeconds(Math.max(1, targetIntervalInSeconds)); // At least 1 second
        } else if (targetIntervalInSeconds <= 3600) {
            return Duration.ofMinutes(Math.max(1, targetIntervalInSeconds / 60));
        } else if (targetIntervalInSeconds <= 86400) {
            return Duration.ofHours(Math.max(1, targetIntervalInSeconds / 3600));
        } else {
            return Duration.ofDays(targetIntervalInSeconds / 86400);
        }
    }


    //calculate distance
    private BigDecimal calculateSpeedPerBike(Bicycle bike) {
        BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
        BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
        // Berechnen der Distanz pro Sekunde
        return realRotationsPerSecond.multiply(circumference);
    }



}

 */
package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    private static final String PROCESSOR_NAME = "Speed";
    private static final BigDecimal CIRCUMFERENCE = new BigDecimal("2.111");
    private static final BigDecimal ROTATION_DIVISOR = new BigDecimal(4);
    private static final int TARGET_DATA_POINTS = 15;
    private static final int MIN_INTERVAL_SECONDS = 1;

    @Autowired
    public SpeedDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        return bikeService.getDataWithTimeSpan(channel, startTime, endTime);
    }

    @Override
    protected List<Bicycle> fetchLastActivity(int channel) {
        return bikeService.getBicyclesSinceLastActivity(channel);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
        if (bicycles.isEmpty()) {
            return new ArrayList<>();
        }

        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));
        Duration intervalSize = determineIntervalSize(bicycles, intervalInSeconds);

        return aggregateSpeedData(bicycles, intervalSize);
    }

    private Duration determineIntervalSize(List<Bicycle> bicycles, int intervalInSeconds) {
        if (intervalInSeconds > 0) {
            return Duration.ofSeconds(intervalInSeconds);
        }

        long totalSeconds = Duration.between(bicycles.get(0).getTime(), bicycles.get(bicycles.size() - 1).getTime()).getSeconds();
        return Duration.ofSeconds(Math.max(totalSeconds / TARGET_DATA_POINTS, MIN_INTERVAL_SECONDS));
    }

    private List<ProcessedData> aggregateSpeedData(List<Bicycle> bicycles, Duration intervalSize) {
        List<ProcessedData> speedData = new ArrayList<>();
        LocalDateTime intervalStart = bicycles.get(0).getTime();
        BigDecimal totalSpeed = BigDecimal.ZERO;
        int count = 0;

        for (Bicycle bike : bicycles) {
            if (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                addProcessedData(speedData, totalSpeed, count, intervalStart);
                intervalStart = bike.getTime();
                totalSpeed = BigDecimal.ZERO;
                count = 0;
            }

            totalSpeed = totalSpeed.add(calculateSpeedPerBike(bike));
            count++;
        }

        addProcessedData(speedData, totalSpeed, count, intervalStart);
        return speedData;
    }

    private BigDecimal calculateSpeedPerBike(Bicycle bike) {
        return bike.getRotations().divide(ROTATION_DIVISOR, 2, RoundingMode.HALF_UP).multiply(CIRCUMFERENCE);
    }

    //muss checken, ob es funktoniert

    private void addProcessedData(List<ProcessedData> data, BigDecimal totalSpeed, int count, LocalDateTime intervalStart) {
        if (count > 0) {
            BigDecimal averageSpeed = totalSpeed.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            data.add(new ProcessedData(data.get(0).getChannel(), averageSpeed,intervalStart, PROCESSOR_NAME));
        }
    }
}
