package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Service
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    public SpeedDataProcessor(BikeService bikeService) {
        super(bikeService);
    }

    @Override
    protected List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime) {

        return this.getBikeService().getDataWithTimeSpan(channel, startTime,endTime);
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        List<ProcessedData> speedData = new ArrayList<>();
        for (Bicycle bike : bicycles) {
            // Berechnen Sie die Geschwindigkeit für jedes Fahrrad
            BigDecimal distance = bike.getRotations().multiply(new BigDecimal("2.111")); // f_t * Umfang
            BigDecimal duration = BigDecimal.valueOf(ChronoUnit.SECONDS.between(bike.getTime().minusSeconds(bike.getTime().getSecond()), bike.getTime())); // t_end - t_start
            BigDecimal speed = distance.divide(duration, RoundingMode.HALF_UP); // v_I = dist_I / duration
            speedData.add(new ProcessedData(bike.getChannel(), speed, bike.getTime()));
        }
        return speedData;
    }


}
