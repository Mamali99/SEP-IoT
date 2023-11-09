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
    private final BikeService bikeService;

    @Autowired
    public OperatingTimeDataProcessor(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        return bikeService.getDataWithTimeSpan(channel, startTime, endTime);
    }
    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> operatingTimeData = new ArrayList<>();
        int counter = 0;
        BigDecimal previousValue = BigDecimal.ZERO;

        for (Bicycle bike : bicycles) {
            if (bike.getRotations().compareTo(BigDecimal.ZERO) != 0) {
                if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
                    counter = 1;
                } else {
                    counter++;
                }
            } else {
                counter = 0;
            }

            previousValue = bike.getRotations();
            operatingTimeData.add(new ProcessedData(bike.getChannel(), BigDecimal.valueOf(counter), bike.getTime()));
        }
        // Zum Testen: Ausgabe der Betriebszeiten auf der Konsole
        for (ProcessedData pd : operatingTimeData) {
            System.out.println("Channel: " + pd.getChannel() + ", Betriebszeit: " + pd.getValue() + ", Zeitstempel: " + pd.getTimestamp());
        }

        return operatingTimeData;
    }
}
