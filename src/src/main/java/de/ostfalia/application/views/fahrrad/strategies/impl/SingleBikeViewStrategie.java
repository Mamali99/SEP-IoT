package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.storedobject.chart.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.ostfalia.application.data.fahrrad.controller.DataAnalysisService;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
public class SingleBikeViewStrategie implements DashboardViewStrategy {

    private final DataAnalysisService dataAnalysisService = new DataAnalysisService();

    public SingleBikeViewStrategie() {
    }

    @Override
    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        List<Component> components = new ArrayList<>();

        Map<String, BigDecimal> result = dataAnalysisService.calculateAverageAndSum(dataList);
        BigDecimal topSpeed = dataAnalysisService.calculateTopSpeed(dataList);
        BigDecimal average = result.get("average");
        BigDecimal sum = result.get("sum");
        BigDecimal roundedSum = sum.setScale(2, RoundingMode.HALF_UP);
        String processorName = dataList.get(0).getProcessorName();
        Integer channel = dataList.get(0).getChannel();

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        VerticalLayout sidePanel = new VerticalLayout();
        sidePanel.add(createTitle(channel));
        sidePanel.add(createMetrics(processorName, roundedSum, average, topSpeed));
        sidePanel.setWidth("30%");


        layout.add(sidePanel);
        layout.add(createLineChart(dataList));

        components.add(layout);
        return components;
    }


    private Component createTitle(Integer channel) {
        H2 h2 = new H2("Bike " + channel);
        Element h2Element = h2.getElement();
        h2.setWidthFull();
        h2Element.getStyle().set("display", "inline-block");
        h2Element.getStyle().set("border", "2px solid #008000");
        return h2;
    }

    private Component createLineChart(List<AbstractDataProcessor.ProcessedData> dataList) {
        // Calculate cumulative values
        Data cumulativeValue = new Data();
        TimeData timestamp = new TimeData();
        BigDecimal cumulativeSum = BigDecimal.ZERO;
        String processorName = dataList.get(0).getProcessorName();

        for (AbstractDataProcessor.ProcessedData processedData : dataList) {
            cumulativeSum = cumulativeSum.add(processedData.getValue());
            cumulativeValue.add(cumulativeSum);
            timestamp.add(processedData.getTimestamp());
        }

        // Plot cumulative line
        LineChart cumulativeLine = new LineChart(timestamp, cumulativeValue);
        cumulativeLine.setName("Cumulated");


        // Plot individual points line
        Data individualValue = new Data();
        for (AbstractDataProcessor.ProcessedData processedData : dataList) {
            individualValue.add(processedData.getValue());
        }

        LineChart individualLine = new LineChart(timestamp, individualValue);
        individualLine.setName("Individual");


        // Reduce number of points on the X-axis
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(9);
        xAxis.setName("Time");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setDivisions(10);
        yAxis.setName(processorName);

        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        cumulativeLine.plotOn(rc);
        individualLine.plotOn(rc);

        SOChart soChart = new SOChart();
        soChart.setSize("90%", "350px");
        soChart.add(new Legend());
        soChart.add(cumulativeLine);
        soChart.add(individualLine);

        return soChart;
    }

    private Aside createMetrics(String processorName, BigDecimal totalDistanceBRounded, BigDecimal average, BigDecimal totalSpeed) {
        Aside aside = new Aside();
        aside.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BoxSizing.BORDER, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE,
                LumoUtility.Position.STICKY);
        aside.setHeight("50%");
        aside.setWidthFull();

        Header headerSection = new Header();
        headerSection.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN, LumoUtility.Margin.Bottom.MEDIUM);

        H3 header = new H3("Metrics");
        header.addClassNames(LumoUtility.Margin.NONE);

        headerSection.add(header);

        UnorderedList ul = new UnorderedList();
        ul.addClassNames(LumoUtility.ListStyleType.NONE,
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.NONE,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Gap.MEDIUM);

        String metricValueEnding = switch (processorName) {
            case "Distance" -> " m";
            case "Speed" -> " m/s";
            case "Rotation" -> " RPM";
            default -> ""; // Rotations per minute

        };

        // Create list items for total distance and average speed
        ListItem totalDistanceTitle = new ListItem("Total " + processorName + ": " + totalDistanceBRounded + metricValueEnding);
        ListItem speed = new ListItem("Average: " + average + metricValueEnding);
        ListItem topSpeed = new ListItem("Top Speed " + totalSpeed + " m/s");


        ul.add(totalDistanceTitle);
        ul.add(speed);
        if (processorName.equals("Speed")) {
            ul.remove(totalDistanceTitle);
            ul.add(topSpeed);
        }
        aside.add(headerSection, ul);

        return aside;
    }
}
