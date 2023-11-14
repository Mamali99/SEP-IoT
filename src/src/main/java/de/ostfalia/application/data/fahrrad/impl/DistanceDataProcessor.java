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

    @Autowired
    public DistanceDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
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

        if (intervalInMinutes == 0) {
            List<ProcessedData> distanceData = new ArrayList<>();
            for (Bicycle bike : bicycles) {
                BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
                BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
                BigDecimal distance = realRotationsPerSecond.multiply(circumference);
                distanceData.add(new ProcessedData(bike.getChannel(), distance, bike.getTime(), processorName));
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

        return intervalDataList;
    }


}
