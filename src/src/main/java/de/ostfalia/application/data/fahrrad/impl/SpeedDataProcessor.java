package de.ostfalia.application.data.fahrrad.impl;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Qualifier("speedDataProcessor")
public class SpeedDataProcessor extends AbstractDataProcessor {

    public SpeedDataProcessor(BikeService bikeService) {
        super(bikeService);
    }

    @Override
    protected List<Bicycle> fetchData() {
        return null;
    }

    @Override
    protected List<ProcessedData> calculateData(List<Bicycle> bicycles) {
        return null;
    }


}
