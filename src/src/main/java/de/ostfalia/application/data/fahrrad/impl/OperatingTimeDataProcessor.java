package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("operatingTimeDataProcessor")
public class OperatingTimeDataProcessor extends AbstractDataProcessor {

    @Autowired
    public OperatingTimeDataProcessor(BikeService bikeService) {
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
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> operatingTimeData = new ArrayList<>();
        BigDecimal sumOperatingTime = BigDecimal.ZERO;
        int operatingPeriods = 0;
        BigDecimal previousValue = BigDecimal.ZERO;

        for (Bicycle bike : bicycles) {
            if (bike.getRotations().compareTo(BigDecimal.ZERO) != 0) {
                if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
                    // Beginn einer neuen Betriebszeitperiode
                    operatingPeriods++;
                    sumOperatingTime = sumOperatingTime.add(BigDecimal.ONE);
                } else {
                    // Fortsetzung einer Betriebszeitperiode
                    sumOperatingTime = sumOperatingTime.add(BigDecimal.ONE);
                }
            } else {
                // Ende einer Betriebszeitperiode, oder das Fahrrad war nicht in Betrieb
            }

            previousValue = bike.getRotations();
            operatingTimeData.add(new ProcessedData(bike.getChannel(), BigDecimal.valueOf(operatingPeriods), bike.getTime()));
        }

        // Berechnung der durchschnittlichen Betriebszeit
        BigDecimal averageOperatingTime = BigDecimal.ZERO;
        if (operatingPeriods > 0) {
            averageOperatingTime = sumOperatingTime.divide(BigDecimal.valueOf(operatingPeriods), 2, BigDecimal.ROUND_HALF_UP);
        }

        // Ausgabe der Gesamt- und Durchschnittsbetriebszeit auf der Konsole
        System.out.println("Gesamtbetriebszeit in Sekunden: " + sumOperatingTime);
        System.out.println("Durchschnittliche Betriebszeit in Sekunden: " + averageOperatingTime);

        return operatingTimeData;
    }

}
