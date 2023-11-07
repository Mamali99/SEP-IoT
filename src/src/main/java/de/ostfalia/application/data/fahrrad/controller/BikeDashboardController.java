package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewContext;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BikeDashboardController {



    private AbstractDataProcessor dataProcessor;

    private DashboardViewContext viewContext;





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
        LocalDateTime startTime = LocalDateTime.parse("2023-08-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
       dataProcessor.process(1, startTime, endTime);
       List<AbstractDataProcessor.ProcessedData> processedDataList = dataProcessor.getResults();
        for (AbstractDataProcessor.ProcessedData data : processedDataList) {
            System.out.println("Channel: " + data.getChannel() +
                    ", Timestamp: " + data.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", speed per second: " + data.getValue());
        }




    }



}
