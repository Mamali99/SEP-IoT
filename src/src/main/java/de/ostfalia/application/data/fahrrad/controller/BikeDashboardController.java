package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.impl.DistanceDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.RotationDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;

import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class BikeDashboardController {



    private AbstractDataProcessor abstractDataProcessor;

    @Autowired
    private DashboardViewContext viewContext;

    @Autowired
    BikeService bikeService;


    public void setDataProcessor(AbstractDataProcessor abstractDataProcessor) {
        this.abstractDataProcessor = abstractDataProcessor;
    }
    public void setMetricProcessor(String metric, int channel, LocalDateTime startTime, LocalDateTime endTime) {
        AbstractDataProcessor processor;
        switch (metric) {
            case "Speed":
                processor = new SpeedDataProcessor(bikeService);
                break;
            case "Rotation":
                processor = new RotationDataProcessor();
                break;
            case "Distance":
                processor = new DistanceDataProcessor();
                break;
            default:
                throw new IllegalArgumentException("Unknown metric: " + metric);
        }

        setDataProcessor(processor);
        updateDashboard(channel, startTime, endTime);
    }


    // Diese Methode wird aufgerufen, um das Dashboard zu aktualisieren.
    public void updateDashboard(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        if(abstractDataProcessor != null) {
            abstractDataProcessor.process(channel, startTime, endTime);
            List<AbstractDataProcessor.ProcessedData> results = abstractDataProcessor.getResults();
            viewContext.buildView(results);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }



}
