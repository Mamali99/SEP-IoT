package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("distanceDataProcessor")
public class DistanceDataProcessor extends AbstractDataProcessor {
    private final BikeService bikeService;

    // Konstante für die Umdrehungslänge
    private static final BigDecimal WHEEL_CIRCUMFERENCE = new BigDecimal("2.111"); // in Metern
    LocalDateTime startTime = LocalDateTime.parse("2023-07-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));



    @Autowired
    public DistanceDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @Override
    protected List<Bicycle> fetchData() {
        return bikeService.getDataWithTimeSpan(1, startTime, endTime);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> processedData = new ArrayList<>();
        for (Bicycle bicycle : bicycles) {
            // Berechne die Distanz für jedes Bicycle-Objekt
            BigDecimal distance = bicycle.getRotations()
                    .multiply(WHEEL_CIRCUMFERENCE)
                    .divide(new BigDecimal("4")); // da 4 Signale pro Umdrehung
            processedData.add(new ProcessedData(bicycle.getChannel(), distance, bicycle.getTime()));
        }
        return processedData;
    }

    @Override
    protected void displayData(List<ProcessedData> processedData) {
        for (ProcessedData data : processedData) {
            System.out.println("Channel: " + data.getChannel() +
                    ", Timestamp: " + data.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", Distance: " + data.getValue() + " m");
        }
    }
}
