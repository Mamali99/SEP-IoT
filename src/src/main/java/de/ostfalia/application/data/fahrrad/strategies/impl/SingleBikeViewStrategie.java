package de.ostfalia.application.data.fahrrad.strategies.impl;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SingleBikeViewStrategie implements DashboardViewStrategy{
    @Override
    public void buildView(List<AbstractDataProcessor.ProcessedData> dataList) {

    }
}
