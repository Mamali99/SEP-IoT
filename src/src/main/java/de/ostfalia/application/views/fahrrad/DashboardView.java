package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;

import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.views.fahrrad.strategies.impl.CompareBikesViewStrategy;

import de.ostfalia.application.views.fahrrad.strategies.impl.SingleBikeViewStrategie;

import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    private final DashboardViewContext context;
    private final BikeDashboardController controller;
    private final BikeService bikeService;

    private ComboBox<String> strategySelector;
    private Button updateButton;
    private ComboBox<Integer> bikeChannelSelector;
    private DateTimePicker startDateTimePicker;
    private DateTimePicker endDateTimePicker;
    private ComboBox<Integer> startSecondSelector;
    private ComboBox<Integer> endSecondSelector;
    private ComboBox<String> metricSelector;

    @Autowired
    public DashboardView(BikeDashboardController bikeDashboardController, DashboardViewContext dashboardViewContext, BikeService bikeService) {
        this.controller = bikeDashboardController;
        this.context = dashboardViewContext;
        this.bikeService = bikeService;

        bikeChannelSelector = new ComboBox<>("Bike Channel");

        List<Integer> availableChannels = bikeService.getAvailableChannels();
        bikeChannelSelector.setItems(availableChannels);

        startDateTimePicker = new DateTimePicker("Start Time");
        endDateTimePicker = new DateTimePicker("End Time");


        startDateTimePicker.setStep(Duration.ofMinutes(1));
        endDateTimePicker.setStep(Duration.ofMinutes(1));


        startSecondSelector = new ComboBox<>("Start Second");
        endSecondSelector = new ComboBox<>("End Second");
        startSecondSelector.setItems(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
        endSecondSelector.setItems(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
        startSecondSelector.setValue(0);
        endSecondSelector.setValue(0);

        metricSelector = new ComboBox<>("Metric");
        metricSelector.setItems("Distance", "Rotation", "Speed");
        metricSelector.addValueChangeListener(event -> updateMetricSelection(event.getValue()));


        this.context.setStrategy(new SingleBikeViewStrategie());

        initializeComponents();
        buildUI();
    }

    private void initializeComponents() {
        strategySelector = new ComboBox<>("View Strategy");
        strategySelector.setItems("Single Bike", "Compare Bikes");
        strategySelector.addValueChangeListener(event -> switchStrategy(event.getValue()));
        updateButton = new Button("Update Dashboard", event -> updateDashboard());

    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout(
                strategySelector,
                bikeChannelSelector,
                metricSelector,
                startDateTimePicker, startSecondSelector,
                endDateTimePicker, endSecondSelector,
                updateButton

        );
        layout.setSizeFull();
        setContent(layout);

    }

    private void switchStrategy(String strategyName) {

        switch (strategyName) {
            case "Single Bike":
                context.setStrategy(new SingleBikeViewStrategie());

                break;
            case "Compare Bikes":
                context.setStrategy(new CompareBikesViewStrategy());
            default:
                throw new IllegalArgumentException("Unknown strategy");
        }

    }



    private void updateMetricSelection(String metric) {

        switch (metric) {
            case "Distance":
                // Implementieren Sie die entsprechende Logik für DistanceDataProcessor
                break;
            case "Rotation":
                // Implementieren Sie die entsprechende Logik für RotationDataProcessor
                break;
            case "Speed":
                // Hier ist bereits die Logik implementiert
                break;
            default:
                Notification.show("Please select a valid metric.");
                break;
        }
    }


    private void updateDashboard() {
        Integer selectedChannel = bikeChannelSelector.getValue();

        LocalDateTime startTime = startDateTimePicker.getValue().withSecond(startSecondSelector.getValue());
        LocalDateTime endTime = endDateTimePicker.getValue().withSecond(endSecondSelector.getValue());



        String selectedMetric = metricSelector.getValue();
        if (selectedMetric == null) {
            Notification.show("Please select a metric.");
            return;
        }


        if (selectedChannel != null && startTime != null && endTime != null && selectedMetric != null) {
            // Rufen Sie die neue Methode im Controller auf
            controller.setMetricProcessor(selectedMetric, selectedChannel, startTime, endTime);
        } else {
            Notification.show("Please select a bike channel, time interval, and metric.");
        }
    }
}