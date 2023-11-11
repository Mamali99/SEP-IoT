package de.ostfalia.application.views.fahrrad.strategies.impl;
import com.storedobject.chart.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.entity.Talsperrendaten;
import de.ostfalia.application.data.fahrrad.controller.DataAnalysisService;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.BasicLayout;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;

import com.vaadin.flow.component.Component;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SingleBikeViewStrategie implements DashboardViewStrategy{

    @Override
    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        List<Component> components = new ArrayList<>();

        VerticalLayout layout = new VerticalLayout();


        Data value = new Data();
        TimeData timestamp = new TimeData();
        Data channel = new Data();


        for (int i = 0; i < 2; i++) {

            System.out.println(dataList.get(i).getProcessorName());
            value.add(dataList.get(i).getValue());
            timestamp.add(dataList.get(i).getTimestamp());
            channel.add(dataList.get(i).getChannel());
        }

        LineChart lineValue = new LineChart(timestamp, value);
        lineValue.setName(dataList.get(0).getProcessorName());

        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(1);
        xAxis.setName("Datum");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName("Wert");
        // Rechtwinklige Achsen
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);

        lineValue.plotOn(rc);
        SOChart soChart = new SOChart();
        soChart.setSize("80%", "300px");
        soChart.add(new Title(dataList.get(0).getProcessorName()));
        soChart.add(new Legend());
        soChart.add(lineValue);
        layout.add(soChart);
        components.add(layout);

        return components;

    }
}
