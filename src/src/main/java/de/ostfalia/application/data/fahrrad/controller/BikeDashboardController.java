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
    private BikeService bikeService;


    public void setDataProcessor(AbstractDataProcessor abstractDataProcessor) {
        this.abstractDataProcessor = abstractDataProcessor;
    }

    public List<AbstractDataProcessor.ProcessedData> getResults() {
        return abstractDataProcessor.getResults();
    }

    public void setMetricProcessor(String metric) {
        AbstractDataProcessor processor = getProcessorForMetric(metric);
        setDataProcessor(processor);
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

    //Durch ein Checkbox überprüfen, ob Glättern durchgeführt werden soll oder nicht
    public void setShouldSmoothData(boolean shouldSmooth) {
        if (abstractDataProcessor != null) {
            abstractDataProcessor.setShouldSmoothData(shouldSmooth);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }


    // Overload for process with start/end time and interval size
    public void updateDashboard(int channel, LocalDateTime startTime, LocalDateTime endTime, int intervalInMinutes) {
        if (abstractDataProcessor != null) {
            abstractDataProcessor.process(channel, startTime, endTime, intervalInMinutes);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }

    // Overload for process with duration and interval size
    public void updateDashboard(int channel, Duration duration, int intervalInMinutes) {
        if (abstractDataProcessor != null) {
            abstractDataProcessor.process(channel, duration, intervalInMinutes);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }

    // Overload for process since last activity with interval size
    // Die Methode werden wir nicht nutzen, obwohl die Funktionalität ist da!!!
    public void updateDashboard(int channel, int intervalInMinutes) {
        if (abstractDataProcessor != null) {
            abstractDataProcessor.process(channel, intervalInMinutes);
        } else {
            throw new IllegalStateException("DataProcessor has not been set");
        }
    }


}
