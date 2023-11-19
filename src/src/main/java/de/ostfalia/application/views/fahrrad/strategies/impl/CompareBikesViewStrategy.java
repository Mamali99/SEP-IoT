package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class CompareBikesViewStrategy implements DashboardViewStrategy {

    private final SingleBikeViewStrategie singleBikeViewStrategie = new SingleBikeViewStrategie();


    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        List<Component> components = new ArrayList<>();

        // Split data into two groups based on channel number
        List<AbstractDataProcessor.ProcessedData> channel1Data = new ArrayList<>();
        List<AbstractDataProcessor.ProcessedData> channel2Data = new ArrayList<>();

        int firstChannel = dataList.get(0).getChannel();
        for (AbstractDataProcessor.ProcessedData processedData : dataList) {
            if (processedData.getChannel() == firstChannel) {
                channel1Data.add(processedData);
            } else {
                channel2Data.add(processedData);
            }
            // Add more conditions if you have more channels
        }

        // Build views for each channel
        List<Component> channel1Components = singleBikeViewStrategie.buildView(channel1Data);
        List<Component> channel2Components = singleBikeViewStrategie.buildView(channel2Data);

        // Create a horizontal layout to display both channel views side by side
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        layout.add(channel1Components.toArray(new Component[0]));
        layout.add(channel2Components.toArray(new Component[0]));

        components.add(layout);
        return components;
    }
}