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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("rotationDataProcessor")
public class RotationDataProcessor extends AbstractDataProcessor {

    String processorName = "Rotation";


    @Autowired
    public RotationDataProcessor(BikeService bikeService) {
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

    /*
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
        List<ProcessedData> rotationData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            BigDecimal rotationsPerSecond = bike.getRotations().divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
            rotationData.add(new ProcessedData(bike.getChannel(), rotationsPerSecond, bike.getTime(), processorName));

        }
        if (this.isShouldSmoothData()) {  // shouldSmoothData ist eine boolesche Variable
            rotationData = smoothData(rotationData, 3);  // windowSize kann konfigurierbar sein
        }
        return rotationData;
    }

     */
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

        // Bestimmen des Intervalls basierend auf Eingabe oder automatisch
        Duration intervalSize;
        if (intervalInSeconds <= 0) {
            intervalSize = bestimmeAutomatischesIntervall(bicycles);
        } else {
            intervalSize = Duration.ofSeconds(intervalInSeconds);
        }

        List<ProcessedData> rotationData = new ArrayList<>();
        LocalDateTime intervalStart = null;
        BigDecimal totalRotations = BigDecimal.ZERO;
        int count = 0;

        for (Bicycle bike : bicycles) {
            // Neues Intervall starten, wenn nötig
            if (intervalStart == null || bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                if (intervalStart != null) {
                    BigDecimal averageRotations = count > 0 ? totalRotations.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    rotationData.add(new ProcessedData(bike.getChannel(), averageRotations, intervalStart, processorName));
                }
                intervalStart = bike.getTime();
                totalRotations = BigDecimal.ZERO;
                count = 0;
            }

            BigDecimal rotationsPerSecond = bike.getRotations().divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
            totalRotations = totalRotations.add(rotationsPerSecond);
            count++;
        }

        // Letztes Intervall hinzufügen
        if (count > 0) {
            BigDecimal averageRotations = totalRotations.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
            rotationData.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), averageRotations, intervalStart, processorName));
        }

        if (this.isShouldSmoothData()) {
            rotationData = smoothData(rotationData, 3);
        }

        return rotationData;
    }

    private Duration bestimmeAutomatischesIntervall(List<Bicycle> bicycles) {
        if (bicycles.isEmpty()) {
            return Duration.ofMinutes(1); // Standardintervall, falls keine Daten vorhanden sind
        }

        LocalDateTime frühesterZeitstempel = bicycles.get(0).getTime();
        LocalDateTime spätesterZeitstempel = bicycles.get(bicycles.size() - 1).getTime();
        long datenZeitspanneInSekunden = Duration.between(frühesterZeitstempel, spätesterZeitstempel).getSeconds();
        long zielIntervallInSekunden = datenZeitspanneInSekunden / 15; // Ziel ist 10-15 Datenpunkte

        if (zielIntervallInSekunden <= 60) {
            return Duration.ofSeconds(Math.max(1, zielIntervallInSekunden)); // Mindestens 1 Sekunde
        } else if (zielIntervallInSekunden <= 3600) {
            return Duration.ofMinutes(Math.max(1, zielIntervallInSekunden / 60));
        } else if (zielIntervallInSekunden <= 86400) {
            return Duration.ofHours(Math.max(1, zielIntervallInSekunden / 3600));
        } else {
            return Duration.ofDays(zielIntervallInSekunden / 86400);
        }
    }


}
