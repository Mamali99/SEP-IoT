package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("operatingTimeDataProcessor")
public class OperatingTimeDataProcessor extends AbstractDataProcessor {

    String processorName = "Operating Time";

    @Autowired
    public OperatingTimeDataProcessor(BikeService bikeService) {
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
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInMinutes) {
        List<ProcessedData> results = new ArrayList<>();

        for (Bicycle bike : bicycles) {
            // Überprüfen, ob das Fahrrad aktiv ist (Rotationen > 0)
            BigDecimal isActive = bike.getRotations().compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            ProcessedData processedData = new ProcessedData(bike.getChannel(), isActive, bike.getTime(), this.processorName);
            results.add(processedData);
        }
        if (this.isShouldSmoothData()) {  // shouldSmoothData ist eine boolesche Variable
            results = smoothData(results, 3);  // windowSize kann konfigurierbar sein
        }

        return results;
    }
*/

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds) {
        // Sortieren der Fahrraddaten nach Zeitstempel
        bicycles.sort((b1, b2) -> b1.getTime().compareTo(b2.getTime()));

        List<ProcessedData> intervalDataList = new ArrayList<>();
        LocalDateTime intervalStart = bicycles.get(0).getTime(); // Startzeit des ersten Intervalls
        Duration intervalSize = Duration.ofSeconds(intervalInSeconds);
        BigDecimal intervalOperatingTime = BigDecimal.ZERO;

        for (Bicycle bike : bicycles) {
            while (bike.getTime().isAfter(intervalStart.plus(intervalSize))) {
                intervalDataList.add(new ProcessedData(bike.getChannel(), intervalOperatingTime, intervalStart, processorName));
                intervalStart = intervalStart.plus(intervalSize);
                intervalOperatingTime = BigDecimal.ZERO;
            }
            // Addieren der Betriebszeit für dieses Intervall
            if (bike.getRotations().compareTo(BigDecimal.ZERO) > 0) {
                intervalOperatingTime = intervalOperatingTime.add(berechneBetriebszeitFuerBike(bike, intervalSize));
            }
        }

        // Daten für das letzte Intervall hinzufügen
        if (intervalOperatingTime.compareTo(BigDecimal.ZERO) > 0) {
            intervalDataList.add(new ProcessedData(bicycles.get(bicycles.size() - 1).getChannel(), intervalOperatingTime, intervalStart, processorName));
        }
        if (this.isShouldSmoothData()) {  // shouldSmoothData ist eine boolesche Variable
            intervalDataList = smoothData(intervalDataList, 3);  // windowSize kann konfigurierbar sein
        }


        return intervalDataList;
    }

    private BigDecimal berechneBetriebszeitFuerBike(Bicycle bike, Duration intervalSize) {

        if (bike.getRotations().compareTo(BigDecimal.ZERO) > 0) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.ZERO;
        }
    }



}
