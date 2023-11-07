package de.ostfalia.application.data.fahrrad.strategies;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardViewContext {

    private DashboardViewStrategy strategy;

    public DashboardViewContext(DashboardViewStrategy strategy){
        this.strategy = strategy;
    }

    public void setStrategy(DashboardViewStrategy strategy) {
        this.strategy = strategy;
    }

    public void buildView(List<AbstractDataProcessor.ProcessedData> processedDataList) {
        strategy.buildView(processedDataList);
    }
}
