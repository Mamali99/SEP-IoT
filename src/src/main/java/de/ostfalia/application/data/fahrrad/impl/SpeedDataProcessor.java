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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    private String processorName = "Speed";


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
            intervalSize = bestimmeAutomatischesIntervall(bicycles);
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

            BigDecimal distance = bike.getRotations().multiply(new BigDecimal("2.111"));
            totalSpeed = totalSpeed.add(distance);
            count++;
        }

        // Letztes Intervall hinzufügen
        if (count > 0) {
            BigDecimal averageSpeed = totalSpeed.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
            speedData.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), averageSpeed, intervalStart, processorName));
        }

        if (this.isShouldSmoothData()) {
            speedData = smoothData(speedData, 3);
        }

        return speedData;
    }


    private Duration bestimmeAutomatischesIntervall(List<Bicycle> bicycles) {
        if (bicycles.isEmpty()) {
            return Duration.ofMinutes(1); // Standardintervall, falls keine Daten vorhanden sind
        }

        LocalDateTime frühesterZeitstempel = bicycles.get(0).getTime();
        LocalDateTime spätesterZeitstempel = bicycles.get(bicycles.size() - 1).getTime();
        long datenZeitspanneInSekunden = Duration.between(frühesterZeitstempel, spätesterZeitstempel).getSeconds();
        long zielIntervallInSekunden = datenZeitspanneInSekunden / 15;

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
