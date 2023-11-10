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

        // Setze die Spalten für die Anzeige
        grid.addColumn(AbstractDataProcessor.ProcessedData::getChannel).setHeader("Channel");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getValue).setHeader("Value");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getTimestamp).setHeader("Timestamp");

        // second grid
        Grid<AbstractDataProcessor.ProcessedData> grid2 = new Grid<>(AbstractDataProcessor.ProcessedData.class);
        grid.setItems(dataList);

        // Setze die Spalten für die Anzeige
        grid.addColumn(AbstractDataProcessor.ProcessedData::getChannel).setHeader("Channel");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getValue).setHeader("Value");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getTimestamp).setHeader("Timestamp");

        // Füge die Tabelle zur Liste der Komponenten hinzu
        components.add(grid);
        components.add(grid2);

        // Weitere Komponenten können ebenfalls hinzugefügt werden, je nach Bedarf

        return components;


    }
}
