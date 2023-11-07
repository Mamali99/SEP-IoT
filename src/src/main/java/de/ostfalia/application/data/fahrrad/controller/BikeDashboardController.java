package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;

import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class BikeDashboardController {


    @Autowired
    private AbstractDataProcessor abstractDataProcessor;

    @Autowired
    private DashboardViewContext viewContext;


    public void setDataProcessor(AbstractDataProcessor abstractDataProcessor) {
        this.abstractDataProcessor = abstractDataProcessor;
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
