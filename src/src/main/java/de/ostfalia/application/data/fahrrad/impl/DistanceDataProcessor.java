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

        List<Bicycle> bicycles = bikeService.getDataWithTimeSpan(channel, startTime, endTime);

        return bicycles;
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
        intervalSize = bestimmeAutomatischesIntervall(bicycles);
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
        intervalValue = intervalValue.add(berechneWertFuerBike(bike));
    }

    if (intervalValue.compareTo(BigDecimal.ZERO) > 0) {
        intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalValue, intervalStart, processorName));
    }

    return intervalDataList;
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





    private BigDecimal berechneWertFuerBike(Bicycle bike) {
        BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
        BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern

        // Berechnen der Distanz pro Sekunde
        BigDecimal distancePerSecond = realRotationsPerSecond.multiply(circumference);

        return distancePerSecond;
    }





}
