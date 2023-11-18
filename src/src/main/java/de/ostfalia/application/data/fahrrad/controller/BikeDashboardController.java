package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.impl.DistanceDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.OperatingTimeDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.RotationDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
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

    public List<AbstractDataProcessor.ProcessedData> getResults() {
        return abstractDataProcessor.getResults();
    }

    // Methode für Standard Start-/Endzeit mit Intervallgröße
    public void setMetricProcessor(String metric, int channel, LocalDateTime startTime, LocalDateTime endTime, int intervalInMinutes) {
        AbstractDataProcessor processor = getProcessorForMetric(metric);
        setDataProcessor(processor);
        abstractDataProcessor.process(channel, startTime, endTime, intervalInMinutes);
    }

    // Überladene Methode für die Dauer mit Intervallgröße
    public void setMetricProcessor(String metric, int channel, Duration duration, int intervalInMinutes) {
        AbstractDataProcessor processor = getProcessorForMetric(metric);
        setDataProcessor(processor);
        abstractDataProcessor.process(channel, duration, intervalInMinutes);
    }

    // Überladene Methode für die letzte Nutzung mit Intervallgröße
    public void setMetricProcessor(String metric, int channel, LocalDateTime sinceTime, boolean sinceLastActivity, int intervalInMinutes) {
        AbstractDataProcessor processor = getProcessorForMetric(metric);
        setDataProcessor(processor);
        if (sinceLastActivity) {
            abstractDataProcessor.processSinceLastActivity(channel, sinceTime, intervalInMinutes);
        } else {
            abstractDataProcessor.process(channel, sinceTime, LocalDateTime.now(), intervalInMinutes);
        }
    }

    private AbstractDataProcessor getProcessorForMetric(String metric) {
        switch (metric) {
            case "Speed":
                return new SpeedDataProcessor(bikeService);
            case "Rotation":
                return new RotationDataProcessor(bikeService);
            case "Distance":
                return new DistanceDataProcessor(bikeService);
            case "Operating time":
                return new OperatingTimeDataProcessor(bikeService);
            default:
                throw new IllegalArgumentException("Unknown metric: " + metric);
        }
    }


    // Methode, um das Dashboard zu aktualisieren (mit Intervallgröße)
    public void updateDashboard(int channel, LocalDateTime startTime, LocalDateTime endTime, int intervalInMinutes) {
        if (abstractDataProcessor != null) {
            abstractDataProcessor.process(channel, startTime, endTime, intervalInMinutes);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }


}
