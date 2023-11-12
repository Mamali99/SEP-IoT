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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Component
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    private String processorName = "Geschwindigkeit";


    @Autowired
    public SpeedDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    // Implementierung für die Standard-Abfrage von Daten zwischen Start- und Endzeit
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
        List<ProcessedData> speedData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            // Berechnen Sie die Geschwindigkeit für jedes Fahrrad
            BigDecimal distance = bike.getRotations().multiply(new BigDecimal("2.111")); // f_t * Umfang
            LocalDateTime endTime = bike.getTime().minusSeconds(bike.getTime().getSecond());
            BigDecimal duration = BigDecimal.valueOf(ChronoUnit.SECONDS.between(endTime, bike.getTime())); // t_end - t_start

            if (duration.compareTo(BigDecimal.ZERO) > 0) { //divide by zero verhindern
                BigDecimal speed = distance.divide(duration, RoundingMode.HALF_UP);
                speedData.add(new ProcessedData(bike.getChannel(), speed, bike.getTime(), processorName));
            }
        }

        return speedData;
    }


}
