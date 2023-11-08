package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.vaadin.flow.component.Component;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;

import java.util.List;

public class MetricViewStrategy implements DashboardViewStrategy {

    @Override
    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        return null;
    }
}
