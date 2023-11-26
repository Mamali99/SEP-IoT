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
@Qualifier("distanceDataProcessor")
public class DistanceDataProcessor extends AbstractDataProcessor {

    private final String processorName = "Distance";


    @Autowired
    public DistanceDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

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

    // Bestimmen das Intervall basierend auf der Eingabe oder berechnen Sie es automatisch
    Duration intervalSize;
    if (intervalInSeconds <= 0) {
        // Automatische Bestimmung des Intervalls, z.B. basierend auf der Größe der Liste
        intervalSize = determineAutomaticInterval(bicycles);
    } else {
        intervalSize = Duration.ofSeconds(intervalInSeconds);
    }

    List<ProcessedData> intervalDataList = new ArrayList<>();
    LocalDateTime intervalStart = bicycles.get(0).getTime();
    BigDecimal intervalValue = BigDecimal.ZERO;

    for (Bicycle bike : bicycles) {
        while (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
            intervalDataList.add(new ProcessedData(bike.getChannel(), intervalValue, intervalStart, processorName));
            intervalStart = intervalStart.plus(intervalSize);
            intervalValue = BigDecimal.ZERO;
        }
        intervalValue = intervalValue.add(calculateDistancePerEntry(bike));
    }

    if (intervalValue.compareTo(BigDecimal.ZERO) > 0) {
        intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalValue, intervalStart, processorName));
    }

    return intervalDataList;
}

    private Duration determineAutomaticInterval(List<Bicycle> bicycles) {
        if (bicycles.isEmpty()) {
            return Duration.ofMinutes(1); // Default interval if no data is available
        }

        LocalDateTime earliestTimestamp = bicycles.get(0).getTime();
        LocalDateTime latestTimestamp = bicycles.get(bicycles.size() - 1).getTime();
        long dataTimespanInSeconds = Duration.between(earliestTimestamp, latestTimestamp).getSeconds();
        long targetIntervalInSeconds = dataTimespanInSeconds / 15; // The goal is 10-15 data points

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






    private BigDecimal calculateDistancePerEntry(Bicycle bike) {
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
@Qualifier("distanceDataProcessor")
public class DistanceDataProcessor extends AbstractDataProcessor {

    private static final String PROCESSOR_NAME = "Distance";
    private static final BigDecimal CIRCUMFERENCE = new BigDecimal("2.111");
    private static final int ROTATION_DIVISOR = 4;
    private static final int TARGET_DATA_POINTS = 15;
    private static final int MINIMUM_INTERVAL_SECONDS = 1;

    @Autowired
    public DistanceDataProcessor(BikeService bikeService) {
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

        List<ProcessedData> intervalDataList = new ArrayList<>();
        LocalDateTime intervalStart = bicycles.get(0).getTime();
        BigDecimal intervalValue = BigDecimal.ZERO;

        for (Bicycle bike : bicycles) {
            intervalValue = updateIntervalData(intervalDataList, intervalValue, intervalStart, intervalSize, bike);
            intervalStart = updateIntervalStart(intervalStart, bike.getTime(), intervalSize);
        }

        addFinalIntervalData(intervalDataList, bicycles, intervalValue, intervalStart);
        return intervalDataList;
    }

    private Duration determineIntervalSize(List<Bicycle> bicycles, int intervalInSeconds) {
        return intervalInSeconds <= 0 ? determineAutomaticInterval(bicycles) : Duration.ofSeconds(intervalInSeconds);
    }

    private Duration determineAutomaticInterval(List<Bicycle> bicycles) {
        LocalDateTime earliestTimestamp = bicycles.get(0).getTime();
        LocalDateTime latestTimestamp = bicycles.get(bicycles.size() - 1).getTime();
        long dataTimespanInSeconds = Duration.between(earliestTimestamp, latestTimestamp).getSeconds();
        long targetIntervalInSeconds = dataTimespanInSeconds / TARGET_DATA_POINTS;

        if (targetIntervalInSeconds <= 60) {
            return Duration.ofSeconds(Math.max(MINIMUM_INTERVAL_SECONDS, targetIntervalInSeconds));
        } else if (targetIntervalInSeconds <= 3600) {
            return Duration.ofMinutes(targetIntervalInSeconds / 60);
        } else if (targetIntervalInSeconds <= 86400) {
            return Duration.ofHours(targetIntervalInSeconds / 3600);
        } else {
            return Duration.ofDays(targetIntervalInSeconds / 86400);
        }
    }

    private BigDecimal updateIntervalData(List<ProcessedData> intervalDataList, BigDecimal intervalValue, LocalDateTime intervalStart, Duration intervalSize, Bicycle bike) {
        if (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
            intervalDataList.add(new ProcessedData(bike.getChannel(), intervalValue, intervalStart, PROCESSOR_NAME));
            return BigDecimal.ZERO;
        }
        return intervalValue.add(calculateDistancePerEntry(bike));
    }

    private LocalDateTime updateIntervalStart(LocalDateTime intervalStart, LocalDateTime bikeTime, Duration intervalSize) {
        while (bikeTime.isAfter(intervalStart.plus(intervalSize))) {
            intervalStart = intervalStart.plus(intervalSize);
        }
        return intervalStart;
    }

    private void addFinalIntervalData(List<ProcessedData> intervalDataList, List<Bicycle> bicycles, BigDecimal intervalValue, LocalDateTime intervalStart) {
        if (intervalValue.compareTo(BigDecimal.ZERO) > 0) {
            intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalValue, intervalStart, PROCESSOR_NAME));
        }
    }

    private BigDecimal calculateDistancePerEntry(Bicycle bike) {
        BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(ROTATION_DIVISOR), 2, RoundingMode.HALF_UP);
        return realRotationsPerSecond.multiply(CIRCUMFERENCE);
    }
}

