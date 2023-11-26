package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Qualifier("operatingTimeDataProcessor")
public class OperatingTimeDataProcessor extends AbstractDataProcessor {

    private static final String PROCESSOR_NAME = "Operating Time";
    private static final int TARGET_DATA_POINTS = 15;
    private static final int MINIMUM_INTERVAL_SECONDS = 1;
    private static final BigDecimal OPERATING_THRESHOLD = BigDecimal.ZERO;

    @Autowired
    public OperatingTimeDataProcessor(BikeService bikeService) {
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
        List<ProcessedData> intervalDataList = new ArrayList<>();
        LocalDateTime intervalStart = bicycles.get(0).getTime();
        BigDecimal intervalOperatingTime = BigDecimal.ZERO;

        for (Bicycle bike : bicycles) {
            intervalOperatingTime = updateIntervalData(intervalDataList, intervalOperatingTime, intervalStart, intervalSize, bike);
            intervalStart = updateIntervalStart(intervalStart, bike.getTime(), intervalSize);
        }

        addFinalIntervalData(intervalDataList, bicycles, intervalOperatingTime, intervalStart);
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

    private BigDecimal updateIntervalData(List<ProcessedData> intervalDataList, BigDecimal intervalOperatingTime, LocalDateTime intervalStart, Duration intervalSize, Bicycle bike) {
        if (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
            intervalDataList.add(new ProcessedData(bike.getChannel(), intervalOperatingTime, intervalStart, PROCESSOR_NAME));
            return BigDecimal.ZERO;
        }
        return bike.getRotations().compareTo(OPERATING_THRESHOLD) > 0 ? intervalOperatingTime.add(BigDecimal.ONE) : intervalOperatingTime;
    }

    private LocalDateTime updateIntervalStart(LocalDateTime intervalStart, LocalDateTime bikeTime, Duration intervalSize) {
        while (bikeTime.isAfter(intervalStart.plus(intervalSize))) {
            intervalStart = intervalStart.plus(intervalSize);
        }
        return intervalStart;
    }

    private void addFinalIntervalData(List<ProcessedData> intervalDataList, List<Bicycle> bicycles, BigDecimal intervalOperatingTime, LocalDateTime intervalStart) {
        if (intervalOperatingTime.compareTo(BigDecimal.ZERO) > 0) {
            intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalOperatingTime, intervalStart, PROCESSOR_NAME));
        }
    }
}

