package de.ostfalia.application.views.fahrrad.strategies;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.stereotype.Component;

import java.util.List;


public interface DashboardViewStrategy {
    void buildView(List<AbstractDataProcessor.ProcessedData> dataList);
}
