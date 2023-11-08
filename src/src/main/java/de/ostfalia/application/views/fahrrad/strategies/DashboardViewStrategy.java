package de.ostfalia.application.views.fahrrad.strategies;

import com.vaadin.flow.component.Component;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;


import java.util.List;


public interface DashboardViewStrategy {
    List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList);
}
