package de.ostfalia.application.views.fahrrad.strategies;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardViewContext {


    private DashboardViewStrategy strategy;

    public DashboardViewContext() {}


    public void setStrategy(DashboardViewStrategy strategy) {
        this.strategy = strategy;
    }

    public List<com.vaadin.flow.component.Component> buildView(List<AbstractDataProcessor.ProcessedData> processedDataList) {
        if(strategy != null) {
            return strategy.buildView(processedDataList);
        } else {
            throw new IllegalStateException("Strategy has not been set");
        }
    }
}
