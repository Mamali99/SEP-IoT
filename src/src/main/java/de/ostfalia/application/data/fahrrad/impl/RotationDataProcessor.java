package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("rotationDataProcessor")
public class RotationDataProcessor extends AbstractDataProcessor {

    private final BikeService bikeService;


    @Autowired
    public RotationDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }
    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        return bikeService.getDataWithTimeSpan(channel, startTime, endTime);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> rotationData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            BigDecimal rotationsPerSecond = bike.getRotations().divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
            rotationData.add(new ProcessedData(bike.getChannel(), rotationsPerSecond, bike.getTime()));
        }
        return rotationData;
    }

}
