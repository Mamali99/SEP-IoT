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
@Qualifier("distanceDataProcessor")
public class DistanceDataProcessor extends AbstractDataProcessor {


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

    /*
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> distanceData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
            BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
            BigDecimal distance = realRotationsPerSecond.multiply(circumference);
            distanceData.add(new ProcessedData(bike.getChannel(), distance, bike.getTime()));
        }

        for(ProcessedData p: distanceData){
            System.out.println("Strecke: " + p.getValue());
        }
        return distanceData;
    }

     */
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        // Sortieren der Fahrraddaten nach Zeitstempel
        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

        // Initialisierung des ersten Intervalls
        LocalDateTime intervalStart = bicycles.get(0).getTime();
        BigDecimal intervalDistance = BigDecimal.ZERO;
        List<ProcessedData> intervalDataList = new ArrayList<>();

        // Bestimmen der Intervallgröße, z.B. 5 Minuten
        Duration intervalSize = Duration.ofMinutes(30);

        // Durchlaufen der Fahrraddaten und Aggregieren der Distanzen in Intervallen
        for (Bicycle bike : bicycles) {
            // Überprüfen, ob das aktuelle Fahrradobjekt zum aktuellen Intervall gehört
            if (bike.getTime().isBefore(intervalStart.plus(intervalSize))) {
                // Aggregieren Sie die Distanz für dieses Intervall
                BigDecimal realRotationsPerSecond = bike.getRotations().divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
                BigDecimal circumference = new BigDecimal("2.111"); // Radumfang in Metern
                BigDecimal distance = realRotationsPerSecond.multiply(circumference).multiply(new BigDecimal("60")); // Distanz pro Minute
                intervalDistance = intervalDistance.add(distance);
            } else {
                // Speichern der aggregierten Daten für das aktuelle Intervall
                intervalDataList.add(new ProcessedData(bike.getChannel(), intervalDistance, intervalStart));

                // Vorbereitung des nächsten Intervalls
                intervalStart = bike.getTime(); // oder intervalStart.plus(intervalSize) für lückenlose Intervalle
                intervalDistance = BigDecimal.ZERO;
            }
        }

        // Stellen Sie sicher, dass Sie die Daten für das letzte Intervall nicht verlieren
        if (!intervalDistance.equals(BigDecimal.ZERO)) {
            intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalDistance, intervalStart));
        }
        for(ProcessedData p: intervalDataList){
            System.out.println("Strecke: " + p.getValue());
        }

        return intervalDataList;
    }


}
