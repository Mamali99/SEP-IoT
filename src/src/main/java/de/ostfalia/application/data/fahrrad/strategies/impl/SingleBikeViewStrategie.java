package de.ostfalia.application.data.fahrrad.strategies.impl;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SingleBikeViewStrategie implements DashboardViewStrategy {
    @Override
    public void buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        // Check if the dataList is not null and has elements
        if (dataList != null && !dataList.isEmpty()) {
            System.out.println("Data List is not empty, size: " + dataList.size());
            // Iterate through the dataList and print out the data
            for (AbstractDataProcessor.ProcessedData data : dataList) {
                System.out.println("Channel: " + data.getChannel() + ", Value: " + data.getValue() + ", Timestamp: " + data.getTimestamp());
            }
        } else {
            System.out.println("Data List is empty or null");
        }
    }
}
