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
import java.util.Comparator;
import java.util.List;

@Component
@Qualifier("rotationDataProcessor")
public class RotationDataProcessor extends AbstractDataProcessor {

    private static final String PROCESSOR_NAME = "Rotation";
    private static final BigDecimal ROTATION_DIVIDER = new BigDecimal("4");
    private static final int DEFAULT_TARGET_DATA_POINTS = 15;
    private static final int MINIMUM_INTERVAL_SECONDS = 1;

    @Autowired
    public RotationDataProcessor(BikeService bikeService) {
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

        bicycles.sort(Comparator.comparing(Bicycle::getTime));
        Duration intervalSize = determineIntervalSize(bicycles, intervalInSeconds);

        return aggregateRotationData(bicycles, intervalSize);
    }

    private Duration determineIntervalSize(List<Bicycle> bicycles, int intervalInSeconds) {
        if (intervalInSeconds > 0) {
            return Duration.ofSeconds(intervalInSeconds);
        }

        LocalDateTime firstTime = bicycles.get(0).getTime();
        LocalDateTime lastTime = bicycles.get(bicycles.size() - 1).getTime();
        long totalSeconds = Duration.between(firstTime, lastTime).getSeconds();
        long targetInterval = totalSeconds / DEFAULT_TARGET_DATA_POINTS;
        return Duration.ofSeconds(Math.max(targetInterval, MINIMUM_INTERVAL_SECONDS));
    }

    private List<ProcessedData> aggregateRotationData(List<Bicycle> bicycles, Duration intervalSize) {
        List<ProcessedData> data = new ArrayList<>();
        LocalDateTime intervalStart = bicycles.get(0).getTime();
        BigDecimal totalRotations = BigDecimal.ZERO;
        int count = 0;

        for (Bicycle bike : bicycles) {
            if (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                BigDecimal averageRotations = calculateAverage(totalRotations, count);
                data.add(new ProcessedData(bike.getChannel(), averageRotations, intervalStart, PROCESSOR_NAME));
                intervalStart = bike.getTime();
                totalRotations = BigDecimal.ZERO;
                count = 0;
            }

            totalRotations = totalRotations.add(bike.getRotations().divide(ROTATION_DIVIDER, 2, RoundingMode.HALF_UP));
            count++;
        }

        if (count > 0) {
            BigDecimal averageRotations = calculateAverage(totalRotations, count);
            data.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), averageRotations, intervalStart, PROCESSOR_NAME));
        }

        return data;
    }

    private BigDecimal calculateAverage(BigDecimal total, int count) {
        return count > 0 ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}

