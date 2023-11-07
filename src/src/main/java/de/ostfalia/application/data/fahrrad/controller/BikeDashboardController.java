package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.impl.DistanceDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.RotationDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.data.repository.bikes.BicycleRepository;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BikeDashboardController {


    private AbstractDataProcessor dataProcessor;

    private DashboardViewContext viewContext;

    private BicycleRepository bicycleRepository;


    public BikeDashboardController(DashboardViewContext viewContext, AbstractDataProcessor dataProcessor, BicycleRepository bicycleRepository) {
        this.viewContext = viewContext;
        this.dataProcessor = dataProcessor;
        this.bicycleRepository = bicycleRepository;
    }

    public void setDataProcessor(String value) {
        if (value != null) {

            this.dataProcessor = switch (value) {
                case "Speed" -> new SpeedDataProcessor(new BikeService(bicycleRepository));
                case "Distance" -> new DistanceDataProcessor(new BikeService(bicycleRepository));
                case "Turns" -> new RotationDataProcessor(new BikeService(bicycleRepository));
                default -> throw new IllegalArgumentException("Unknown metric");
            };
        }
    }

    public AbstractDataProcessor getDataProcessor() {
        return this.dataProcessor;
    }

    public DashboardViewContext getViewContext() {
        return viewContext;
    }

    public void setViewContext(DashboardViewContext viewContext) {
        this.viewContext = viewContext;
    }

    public void updateDashboard(int channel, LocalDateTime startTime, LocalDateTime endTime) {

        dataProcessor.process(channel, startTime, endTime); // Hier werden die Daten verarbeitet
        List<AbstractDataProcessor.ProcessedData> processedDataList = dataProcessor.getResults();
        // Führe hier die Aktualisierung der GUI durch, um die neuen Daten anzuzeigen
        viewContext.buildView(processedDataList);

    }

}