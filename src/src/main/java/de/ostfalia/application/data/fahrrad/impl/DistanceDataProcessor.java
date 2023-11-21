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


/*
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInMinutes) {
        if (intervalInMinutes == 0) {
            List<ProcessedData> distanceData = new ArrayList<>();
            for (Bicycle bike : bicycles) {
                BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
                BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
                BigDecimal distance = realRotationsPerSecond.multiply(circumference);
                distanceData.add(new ProcessedData(bike.getChannel(), distance, bike.getTime(), processorName));
            }
            if (this.isShouldSmoothData()) {  // shouldSmoothData ist eine boolesche Variable
                distanceData = smoothData(distanceData, 3);  // windowSize kann konfigurierbar sein
            }
            return distanceData;

        }
        // Sortieren der Fahrraddaten nach Zeitstempel
        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

        List<ProcessedData> intervalDataList = new ArrayList<>();
        if (!bicycles.isEmpty()) {
            // Initialisierung des ersten Intervalls
            LocalDateTime intervalStart = bicycles.get(0).getTime();
            BigDecimal intervalDistance = BigDecimal.ZERO;
            BigDecimal totalDistance = BigDecimal.ZERO;

            // Bestimmen der Intervallgröße
            Duration intervalSize = Duration.ofMinutes(intervalInMinutes);

            // Durchlaufen der Fahrraddaten und Aggregieren der Distanzen in Intervallen
            for (Bicycle bike : bicycles) {
                // Überprüfen, ob das aktuelle Fahrradobjekt zum nächsten Intervall gehört
                while (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                    // Speichern der aggregierten Daten für das aktuelle Intervall
                    intervalDataList.add(new ProcessedData(bike.getChannel(), intervalDistance, intervalStart, processorName));

                    // Vorbereitung des nächsten Intervalls
                    intervalStart = intervalStart.plus(intervalSize);
                    intervalDistance = BigDecimal.ZERO;
                }
                // Aggregieren Sie die Distanz für dieses Intervall
                BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
                BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
                BigDecimal distance = realRotationsPerSecond.multiply(circumference); // Distanz pro Minute
                intervalDistance = intervalDistance.add(distance);
                totalDistance = totalDistance.add(distance);
            }

            // Stellen Sie sicher, dass Sie die Daten für das letzte Intervall nicht verlieren
            if (intervalDistance.compareTo(BigDecimal.ZERO) > 0) {
                intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalDistance, intervalStart, processorName));
            }
        }

        // Ausgabe der Intervallstrecken
        intervalDataList.forEach(p -> System.out.println("Interval Start: " + p.getTimestamp() + " Strecke: " + p.getValue() + "test" + p.getProcessorName()));

        if (this.isShouldSmoothData()) {  // shouldSmoothData ist eine boolesche Variable
            intervalDataList = smoothData(intervalDataList, 3);  // windowSize kann konfigurierbar sein
        }
        return intervalDataList;
    }

 */
    /*
@Override
protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
    // Sortieren der Fahrraddaten nach Zeitstempel
    bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

    List<ProcessedData> intervalDataList = new ArrayList<>();
    LocalDateTime intervalStart = bicycles.get(0).getTime(); // Startzeit des ersten Intervalls
    BigDecimal intervalValue = BigDecimal.ZERO;
    Duration intervalSize = Duration.ofSeconds(intervalInSeconds);

    for (Bicycle bike : bicycles) {
        while (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
            intervalDataList.add(new ProcessedData(bike.getChannel(), intervalValue, intervalStart, processorName));
            intervalStart = intervalStart.plus(intervalSize);
            intervalValue = BigDecimal.ZERO;
        }
        // Fügen Sie hier die Logik hinzu, um die Werte für jedes Intervall zu aggregieren
        intervalValue = intervalValue.add(berechneWertFuerBike(bike));
    }

    // Fügen Sie Daten für das letzte Intervall hinzu, falls vorhanden
    if (intervalValue.compareTo(BigDecimal.ZERO) > 0) {
        intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalValue, intervalStart, processorName));
    }

    return intervalDataList;
}

     */
@Override
protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
    bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

    // Bestimmen Sie das Intervall basierend auf der Eingabe oder berechnen Sie es automatisch
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
        // die Logik zur automatischen Bestimmung des Intervalls
        // Beispiel: Wenn die Liste groß ist, ein größeres Intervall, sonst ein kleineres wählen
        int listSize = bicycles.size();
        if (listSize < 600) { //für 10 Minuten
            return Duration.ofMinutes(1); // Beispiel für kleinere Datenmengen
        } else {
            return Duration.ofHours(1); // Beispiel für größere Datenmengen
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
