package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;

import java.util.ArrayList;
import java.util.List;

public class CompareBikesViewStrategy implements DashboardViewStrategy {

    @Override
    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        List<Component> components = new ArrayList<>();

        Grid<AbstractDataProcessor.ProcessedData> grid = new Grid<>(AbstractDataProcessor.ProcessedData.class);
        grid.setItems(dataList);

        grid.addColumn(AbstractDataProcessor.ProcessedData::getChannel).setHeader("Channel");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getValue).setHeader("Value");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getTimestamp).setHeader("Timestamp");

        components.add((Component) grid);


        return components;


    }
}
