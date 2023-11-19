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

    private String processorName = "Distance";

    LocalDateTime start;
    LocalDateTime end;

    @Autowired
    public DistanceDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        this.start = startTime;
        this.end = endTime;

        return bikeService.getDataWithTimeSpan(channel, startTime, endTime);
    }

    // Implementierung für die Abfrage von Daten seit einem bestimmten Zeitpunkt
    @Override
    protected List<Bicycle> fetchDataSince(int channel, LocalDateTime sinceTime) {
        return bikeService.findBicycleDataSince(channel, sinceTime);
    }

    // Implementierung für das Abrufen der letzten Aktivität eines Kanals
    @Override
    protected LocalDateTime fetchLastActivity(int channel) {
        return bikeService.findLastActivityByChannel(channel);
    }



    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInMinutes) {
        List<ProcessedData> results = new ArrayList<>();
        BigDecimal distancePerRotation = new BigDecimal("2.111"); // Distanz pro Rotation

        for (Bicycle bike : bicycles) {
            // Berechnung der Distanz für dieses Fahrrad
            BigDecimal distance = bike.getRotations().multiply(distancePerRotation);

            // Runden der Distanz
            distance = distance.setScale(2, RoundingMode.HALF_UP);

            // Erstellen des ProcessedData Objekts
            ProcessedData processedData = new ProcessedData(bike.getChannel(), distance, bike.getTime(), this.processorName);
            results.add(processedData);
        }

        return results;
    }



}
