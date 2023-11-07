package de.ostfalia.application.data.fahrrad.strategies;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;

import java.util.List;

public interface DashboardViewStrategy {

    void buildView(List<AbstractDataProcessor.ProcessedData> dataList);
}
