package de.ostfalia.application.views.fahrrad.strategies.impl;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class CompareBikesViewStrategy implements DashboardViewStrategy {
    @Override
    public void buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        System.out.println("size form Compare View: " + dataList.size());
    }
}
