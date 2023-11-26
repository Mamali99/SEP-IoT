package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.storedobject.chart.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.ostfalia.application.data.fahrrad.controller.DataAnalysisService;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        Icon bicycleIcon = VaadinIcon.CHART.create();
        bicycleIcon.setSize("24px"); // Set the size as needed

        H2 h2 = new H2("Bike " + channel);
        h2.getStyle().set("padding-left", "8px");

        Div titleWrapper = new Div(bicycleIcon, h2);
        titleWrapper.getStyle().set("display", "flex");
        titleWrapper.getStyle().set("align-items", "center");
        titleWrapper.getStyle().set("margin-top", "40px");

        return titleWrapper;
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
        cumulativeLine.setName("Total " + processorName + " values");


        // Plot individual points line
        Data individualValue = new Data();
        for (AbstractDataProcessor.ProcessedData processedData : dataList) {
            individualValue.add(processedData.getValue());
        }

        LineChart individualLine = new LineChart(timestamp, individualValue);
        individualLine.setName("Individual " + processorName + " values");


        // Reduce number of points on the X-axis
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(9);
        xAxis.setName("Time");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setDivisions(10);
        yAxis.setName(processorName);

        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);


        if (!processorName.equals("Speed")) {
            cumulativeLine.plotOn(rc);
        }
        individualLine.plotOn(rc);


        SOChart soChart = new SOChart();
        soChart.setSize("100%", "350px");
        soChart.add(new Legend());
        if (!processorName.equals("Speed")) {
            soChart.add(cumulativeLine);
        }
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




        ListItem totalDistanceTitle = new ListItem(formatMetric(totalDistanceBRounded, processorName, "Total"));
        ListItem speed = new ListItem(formatMetric(average, processorName, "Average"));
        ListItem topSpeed = new ListItem(formatMetric(totalSpeed, processorName, "Top Speed"));


        ul.add(totalDistanceTitle);
        ul.add(speed);
        if (processorName.equals("Speed")) {
            ul.remove(totalDistanceTitle);
            ul.add(topSpeed);
        }
        aside.add(headerSection, ul);

        return aside;
    }

    private String formatOperatingTime(BigDecimal operatingTime) {
        long seconds = operatingTime.longValue();

        if (seconds < 60) {
            return seconds + " s";
        } else if (seconds < 3600) {
            long minutes = TimeUnit.SECONDS.toMinutes(seconds);
            long remainingSeconds = seconds % 60;
            if (remainingSeconds > 0) {
                return minutes + " min " + remainingSeconds + " s";
            } else {
                return minutes + " min";
            }
        } else if (seconds < 86400) {
            long hours = TimeUnit.SECONDS.toHours(seconds);
            long remainingMinutes = (seconds % 3600) / 60;
            if (remainingMinutes > 0) {
                return hours + " h " + remainingMinutes + " min";
            } else {
                return hours + " h";
            }
        } else {
            long days = TimeUnit.SECONDS.toDays(seconds);
            long remainingHours = (seconds % 86400) / 3600;
            if (remainingHours > 0) {
                return days + " days " + remainingHours + " h";
            } else {
                return days + " days";
            }
        }
    }

    private String formatMetric(BigDecimal value, String processorName, String metricType) {
        String metricValueEnding;

        switch (processorName) {
            case "Distance":
                metricValueEnding = value + " m";
                break;
            case "Speed":
                metricValueEnding = value + " m/s";
                break;
            case "Rotation":
                metricValueEnding = value + " RPM";
                break;
            case "Operating Time":
                metricValueEnding = formatOperatingTime(value);
                break;
            default:
                metricValueEnding = "";
        }

        switch (metricType) {
            case "Total":
                return "Total " + processorName + ": " + metricValueEnding;
            case "Average":
                return "Average: " + metricValueEnding;
            case "Top Speed":
                return "Top Speed " + metricValueEnding;
            default:
                return "";
        }
    }

}
