package de.ostfalia.application.views.fahrrad.strategies.impl;
import com.vaadin.flow.component.grid.Grid;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.BasicLayout;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class SingleBikeViewStrategie implements DashboardViewStrategy {


    @Override
    public void buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        System.out.println("size form Single View: " + dataList.size());
    }
}
