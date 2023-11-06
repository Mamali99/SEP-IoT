package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("rotationDataProcessor")
public class RotationDataProcessor extends AbstractDataProcessor {
    @Autowired
    private BikeService bikeService;
    List<Bicycle> bicycleList;
    LocalDateTime startTime = LocalDateTime.parse("2023-08-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));




    @Override
    protected List<Bicycle> fetchData() {
         this.bicycleList = bikeService.getDataWithTimeSpan(1, startTime, endTime);
        return bicycleList;
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> p = new ArrayList<>();
        for(Bicycle b: bicycles){
            p.add(new ProcessedData(b.getChannel(), b.getRotations(), b.getTime()));
        }
        return p;
    }

    @Override
    protected void displayData(List<ProcessedData> processedData) {

        for (ProcessedData bicycle : processedData) {
            System.out.println("Channel: " + bicycle.getChannel() +
                    ", Timestamp: " + bicycle.getTimestamp() +
                    ", Rotations per second: " + bicycle.getValue());
        }

    }
}
