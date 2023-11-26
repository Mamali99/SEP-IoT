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

        bicycles.sort(Comparator.comparing(Bicycle::getTime));
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
        int channel= bicycles.get(0).getChannel();

        for (Bicycle bike : bicycles) {
            if (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                addProcessedData(channel, speedData, totalSpeed, count, intervalStart);
                intervalStart = bike.getTime();
                totalSpeed = BigDecimal.ZERO;
                count = 0;
            }

            totalSpeed = totalSpeed.add(calculateSpeedPerBike(bike));
            count++;
        }

        addProcessedData(channel, speedData, totalSpeed, count, intervalStart);
        return speedData;
    }

    private BigDecimal calculateSpeedPerBike(Bicycle bike) {
        return bike.getRotations().divide(ROTATION_DIVISOR, 2, RoundingMode.HALF_UP).multiply(CIRCUMFERENCE);
    }


    private void addProcessedData(int channel, List<ProcessedData> data, BigDecimal totalSpeed, int count, LocalDateTime intervalStart) {
        if (count > 0) {
            BigDecimal averageSpeed = totalSpeed.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            data.add(new ProcessedData(channel, averageSpeed,intervalStart, PROCESSOR_NAME));
        }
    }
}
