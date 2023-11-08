package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class SingleBikeViewStrategie extends VerticalLayout implements DashboardViewStrategy {
    private Grid<AbstractDataProcessor.ProcessedData> grid;
    public SingleBikeViewStrategie() {
        grid = new Grid<>(AbstractDataProcessor.ProcessedData.class, false);
        grid.addColumn(AbstractDataProcessor.ProcessedData::getChannel).setHeader("Channel");
        grid.addColumn(data -> data.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).setHeader("Timestamp");
        grid.addColumn(AbstractDataProcessor.ProcessedData::getValue).setHeader("Speed per second");
        add(grid);
    }

    @Override
    public void buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        grid.setItems(dataList);
    }
}
