package de.ostfalia.application.views.fahrrad.strategies.impl;

import com.storedobject.chart.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
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

@org.springframework.stereotype.Component
public class SingleBikeViewStrategie implements DashboardViewStrategy {

    private final DataAnalysisService dataAnalysisService = new DataAnalysisService();

    public SingleBikeViewStrategie() {
    }

    @Override
    public List<Component> buildView(List<AbstractDataProcessor.ProcessedData> dataList) {
        List<Component> components = new ArrayList<>();

        Map<String, BigDecimal> result = dataAnalysisService.calculateAverageAndSum(dataList);
        BigDecimal average = result.get("average");
        BigDecimal sum = result.get("sum");
        String processorName = dataList.get(0).getProcessorName();


        VerticalLayout layout = new VerticalLayout();


        Data value = new Data();
        TimeData timestamp = new TimeData();

        for (AbstractDataProcessor.ProcessedData processedData : dataList) {
            value.add(processedData.getValue());
            timestamp.add(processedData.getTimestamp());
        }

        BigDecimal totalDistanceBD = new BigDecimal(String.valueOf(sum));
        BigDecimal totalDistanceBRounded = totalDistanceBD.setScale(2, RoundingMode.DOWN);
        BigDecimal totalSpeedBD = new BigDecimal(String.valueOf(average));
        BigDecimal totalSpeedBRounded = totalSpeedBD.setScale(2, RoundingMode.DOWN);

        H2 title = new H2("Fahrrad " + dataList.get(0).getChannel());

        layout.add(title);

        LineChart lineValue = new LineChart(timestamp, value);
        lineValue.setName(processorName);
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(1);
        xAxis.setName("Zeit");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName(processorName);
        // Rechtwinklige Achsen
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);

        layout.add(title);
        ListItem totalDistanceTitle = new ListItem("Gesamtsumme der " + processorName + ": " + totalDistanceBRounded);
        ListItem speed = new ListItem("Avg.: " + totalSpeedBRounded);

        Aside aside = new Aside();
        aside.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BoxSizing.BORDER, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE,
                LumoUtility.Position.STICKY);
        Header headerSection = new Header();
        headerSection.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN, LumoUtility.Margin.Bottom.MEDIUM);
        H3 header = new H3("Kennzahlen");
        header.addClassNames(LumoUtility.Margin.NONE);
        headerSection.add(header);

        UnorderedList ul = new UnorderedList();
        ul.addClassNames(LumoUtility.ListStyleType.NONE,
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.NONE,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Gap.MEDIUM);

        ul.add(totalDistanceTitle);
        ul.add(speed);
        aside.add(headerSection, ul);


        lineValue.plotOn(rc);
        SOChart soChart = new SOChart();
        soChart.setSize("80%", "300px");
        soChart.add(new Legend());
        soChart.add(lineValue);

        HorizontalLayout graphAndMetrics = new HorizontalLayout();
        graphAndMetrics.setSizeFull();
        graphAndMetrics.add(aside, soChart);


        //layout.add(soChart);
        //layout.add(totalDistanceTitle);
        //layout.add(speed);
        //layout.add(aside);
        layout.add(graphAndMetrics);
        components.add(layout);

        if (processorName.equals("Geschwindigkeit")) {
            graphAndMetrics.remove(totalDistanceTitle);
        }
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
                BigDecimal currentSpeed = dataList.get(i).getValue();
                totalSpeed = totalSpeed.max(currentSpeed);
                System.out.println(currentSpeed);
            }
        }
        return totalSpeed;
    }

}
