package de.ostfalia.application.views.fahrrad.strategies.impl;
import com.storedobject.chart.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
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
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;


import java.math.BigDecimal;
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

        for (int i = 0; i < dataList.size(); i++) {
            value.add(dataList.get(i).getValue());
            timestamp.add(dataList.get(i).getTimestamp());
            channel.add(dataList.get(i).getChannel());
            calculateTotalDistance(dataList);
            calculateTotalSpeed(dataList);

        }

        BigDecimal totalDistanceBD = new BigDecimal(String.valueOf(calculateTotalDistance(dataList)));
        BigDecimal totalDistanceBDrounded = totalDistanceBD.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal totalSpeedBD = new BigDecimal(String.valueOf(calculateTotalSpeed(dataList)));
        BigDecimal totalSpeedBDrounded = totalSpeedBD.setScale(2, BigDecimal.ROUND_DOWN);

        H2 title = new H2(dataList.get(0).getProcessorName() + " für Fahrrad " + dataList.get(0).getChannel());

        layout.add(title);

        LineChart lineValue = new LineChart(timestamp, value);
        lineValue.setName(dataList.get(0).getProcessorName());
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(1);
        xAxis.setName("Datum");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName("Wert");
        // Rechtwinklige Achsen
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);

        layout.add(title);
        H3 totalDistanceTitle = new H3("Gesamtstrecke: " + totalDistanceBDrounded + " Meter");
        H3 speed = new H3("Max Geschwindigeit: " + totalSpeedBDrounded + " Meter pro min");
        lineValue.plotOn(rc);
        SOChart soChart = new SOChart();
        soChart.setSize("80%", "300px");
        soChart.add(new Legend());
        soChart.add(lineValue);
        layout.add(soChart);
        layout.add(totalDistanceTitle);
        layout.add(speed);
        components.add(layout);

        return components;

    }

    private BigDecimal calculateTotalDistance(List<AbstractDataProcessor.ProcessedData> dataList) {
        BigDecimal totalDistance = BigDecimal.ZERO;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getProcessorName().equals("Distanz")) {
                totalDistance = totalDistance.add(dataList.get(i).getValue());
            }
        }
        return totalDistance;
    }

    private BigDecimal calculateTotalSpeed(List<AbstractDataProcessor.ProcessedData> dataList) {
        BigDecimal totalSpeed = BigDecimal.ZERO;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getProcessorName().equals("Geschwindigkeit")) {
                totalSpeed = totalSpeed.add(dataList.get(i).getValue().max(dataList.get(i).getValue()));
                System.out.println(dataList.get(i).getValue());
            }
        }
        return totalSpeed;
    }

}
