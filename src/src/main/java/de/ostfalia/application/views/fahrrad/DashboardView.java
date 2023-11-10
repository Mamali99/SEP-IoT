package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;

import de.ostfalia.application.data.fahrrad.controller.DataAnalysisService;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.views.fahrrad.strategies.impl.CompareBikesViewStrategy;

import de.ostfalia.application.views.fahrrad.strategies.impl.SingleBikeViewStrategie;

import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
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

    //Für Intervallgröße
    private NumberField intervalSizeField;
    private VerticalLayout layout;

    // Neue UI-Komponenten für die Dauer hinzufügen
    private NumberField durationValueField;
    private ComboBox<String> durationUnitSelector;

    @Autowired
    private DataAnalysisService dataAnalysisService;

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
        metricSelector.setItems("Distance", "Rotation", "Speed", "Operating time");
        metricSelector.addValueChangeListener(event -> updateMetricSelection(event.getValue()));
        this.context.setStrategy(new SingleBikeViewStrategie());

        // Komponenten für die Dauerinitialisierung
        durationValueField = new NumberField("Duration Value");
        durationUnitSelector = new ComboBox<>("Duration Unit", "Minutes", "Hours", "Days");
        durationUnitSelector.setValue("Minutes"); // Setze Standardwert

        // Initialisieren des neuen Feldes für die Intervallgröße
        intervalSizeField = new NumberField("Interval Size (minutes)");
        intervalSizeField.setValue(5.0); // Standardwert ist 5 Minuten
        intervalSizeField.setMin(1);

        //-------------------------------Defaultwerte setzen-------------------------------------
        // Festlegen der Standardwerte für das Start- und Enddatum/-zeit

        LocalDateTime defaultStartTime = LocalDateTime.of(2023, 9, 8, 16, 8, 1); // 8. September 2023, 16:08:01
        LocalDateTime defaultEndTime = LocalDateTime.of(2023, 9, 8, 16, 8, 31); // 8. September 2023, 16:08:31
        startDateTimePicker.setValue(defaultStartTime.minusSeconds(defaultStartTime.getSecond()));
        endDateTimePicker.setValue(defaultEndTime.minusSeconds(defaultEndTime.getSecond()));
        startSecondSelector.setValue(defaultStartTime.getSecond());
        endSecondSelector.setValue(defaultEndTime.getSecond());
        // Festlegen des Standardkanals und der Standardmetrik
        bikeChannelSelector.setValue(1); // Standardkanal 1
        metricSelector.setValue("Distance"); // Standardmetrik "Distance"
        //-----------------------------------------------------------------------------------------


        initializeComponents();
        buildUI();
    }

    private void initializeComponents() {
        strategySelector = new ComboBox<>("View Strategy");
        strategySelector.setItems("Single Bike", "Compare Bikes");
        // Set "Single Bike" as the default selected value
        strategySelector.setValue("Single Bike");
        strategySelector.addValueChangeListener(event -> switchStrategy(event.getValue()));
        updateButton = new Button("Update Dashboard", event -> updateDashboard());

    }

    private void buildUI() {
        layout = new VerticalLayout(
                strategySelector,
                bikeChannelSelector,
                metricSelector, durationValueField, durationUnitSelector,intervalSizeField,
                startDateTimePicker, startSecondSelector,
                endDateTimePicker, endSecondSelector,
                updateButton

        );
        //layout.setSizeFull();
        setContent(layout);

    }

    private void switchStrategy(String strategyName) {

        switch (strategyName) {
            case "Single Bike":
                context.setStrategy(new SingleBikeViewStrategie());
                break;
            case "Compare Bikes":
                context.setStrategy(new CompareBikesViewStrategy());
                break;
            default:
                throw new IllegalArgumentException("Unknown strategy");
        }

    }



    private void updateMetricSelection(String metric) {

        switch (metric) {
            case "Distance":
                break;
            case "Rotation":

                break;
            case "Speed":

                break;
            case "Operating time":
                break;
            default:
                Notification.show("Please select a valid metric.");
                break;
        }
    }


    private void updateDashboard() {
        Integer selectedChannel = bikeChannelSelector.getValue();
        String selectedMetric = metricSelector.getValue();

        int intervalSizeInMinutes = intervalSizeField.getValue().intValue();

        if (selectedMetric == null) {
            Notification.show("Please select a metric.");
            return;
        }

        // Berechnet die Dauer basierend auf den Eingaben des Benutzers
        Duration duration = null;
        if (durationValueField != null && durationValueField.getValue() != null) {
            long durationValue = durationValueField.getValue().longValue();
            String durationUnit = durationUnitSelector.getValue();
            if ("Minutes".equals(durationUnit)) {
                duration = Duration.ofMinutes(durationValue);
            } else if ("Hours".equals(durationUnit)) {
                duration = Duration.ofHours(durationValue);
            }else if("Days".equals(durationUnit)){
                duration = Duration.ofDays(durationValue);
            }

        }

        List<AbstractDataProcessor.ProcessedData> results;

        // Wenn eine Dauer gewählt wurde, benutzen Sie diese zur Datenverarbeitung
        if (selectedChannel != null && duration != null) {
            controller.setMetricProcessor(selectedMetric, selectedChannel, duration, intervalSizeInMinutes);
            results = controller.getResults();
        }
        // Andernfalls benutzen Sie das Start- und Enddatum zur Datenverarbeitung
        else if (selectedChannel != null && startDateTimePicker.getValue() != null && endDateTimePicker.getValue() != null) {
            LocalDateTime startTime = startDateTimePicker.getValue().withSecond(startSecondSelector.getValue());
            LocalDateTime endTime = endDateTimePicker.getValue().withSecond(endSecondSelector.getValue());
            controller.setMetricProcessor(selectedMetric, selectedChannel, startTime, endTime, intervalSizeInMinutes);
            results = controller.getResults();
        } else {
            Notification.show("Please select a bike channel and a time interval or duration.");
            return;
        }

        // Nachdem die Daten verarbeitet wurden, bauen Sie die Ansicht auf und zeigen Sie die Ergebnisse an
        if (results != null && !results.isEmpty()) {
            // Hier könnte die Datenanalyse durchgeführt werden
            DataAnalysisService.AnalysisResult analysisResult = dataAnalysisService.calculateAverageAndSum(results);

            // Aktualisieren Sie die Komponenten mit den neuen Daten
            List<Component> components = context.buildView(results);
            layout.removeAll();
            buildUI();
            HorizontalLayout rightLayout = new HorizontalLayout();
            rightLayout.setWidth("100%");
            rightLayout.add(components);
            layout.add(rightLayout);
        } else {
            Notification.show("No data available for the selected criteria.");
        }

    }
}