package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceDataProcessor extends AbstractDataProcessor {


    public DistanceDataProcessor(BikeService bikeService) {
        super(bikeService);
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {

        return this.getBikeService().getDataWithTimeSpan(channel, startTime, endTime);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> distanceData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            // Berechne die zurückgelegte Strecke für jedes Fahrrad
            BigDecimal distance = bike.getRotations().multiply(new BigDecimal("2.111")); // f_t * Umfang
            distanceData.add(new ProcessedData(bike.getChannel(), distance, bike.getTime()));
        }
        return distanceData;
    }


}
