package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewContext;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeDashboardController {



    private AbstractDataProcessor dataProcessor;

    private DashboardViewContext viewContext;


    public BikeDashboardController(DashboardViewContext viewContext) {
        this.viewContext = viewContext;

    }

    public BikeDashboardController(AbstractDataProcessor dataProcessor){
        this.dataProcessor = dataProcessor;
    }
    public BikeDashboardController(){}
    public void setDataProcessor(AbstractDataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    public AbstractDataProcessor getDataProcessor() {
        return dataProcessor;
    }

    public DashboardViewContext getViewContext() {
        return viewContext;
    }

    public void setViewContext(DashboardViewContext viewContext) {
        this.viewContext = viewContext;
    }

    public void updateDashboard(){


    }



}
