package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.material.Material;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;

import de.ostfalia.application.data.fahrrad.controller.DataAnalysisService;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.BasicLayout;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.views.fahrrad.strategies.impl.CompareBikesViewStrategy;
import de.ostfalia.application.views.fahrrad.strategies.impl.SingleBikeViewStrategie;
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

    private HorizontalLayout strategySelector;
    private Button updateButton;
    private ComboBox<Integer> bikeChannelSelector;
    private DateTimePicker startDateTimePicker;
    private DateTimePicker endDateTimePicker;
    private ComboBox<Integer> startSecondSelector;
    private ComboBox<Integer> endSecondSelector;
    private ListBox<String> metricSelector;
    private VerticalLayout layout;
    SplitLayout splitLayout;
    HorizontalLayout titleGroup;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    public DashboardView(BikeDashboardController bikeDashboardController, DashboardViewContext dashboardViewContext, BikeService bikeService) {
        this.controller = bikeDashboardController;
        this.context = dashboardViewContext;
        this.bikeService = bikeService;

        splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(30);
        splitLayout.setSizeFull();

        titleGroup = new HorizontalLayout();
        Icon dashicon = VaadinIcon.DASHBOARD.create();
        H2 title = new H2("Bike Dashboard");
        titleGroup.add(dashicon, title);

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

        metricSelector = new ListBox<>();
        metricSelector.setItems("Distance", "Rotation", "Speed", "Operating Time");
        metricSelector.setValue("Speed");
        metricSelector.addValueChangeListener(event -> updateMetricSelection(event.getValue()));


        this.context.setStrategy(new SingleBikeViewStrategie());

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
        // Create the buttons
        Button singleBikeButton = new Button("Single Bike");
        Button compareBikesButton = new Button("Compare Bikes");

        // Apply styles to make the buttons orange with white text and rounded corners
        singleBikeButton.getStyle().set("background-color", "#FFA500"); // Orange color in hex
        singleBikeButton.getStyle().set("color", "white");
        singleBikeButton.getStyle().set("border-radius", "12px"); // Adjust the value as needed

        compareBikesButton.getStyle().set("background-color", "#FFA500"); // Orange color in hex
        compareBikesButton.getStyle().set("color", "white");
        compareBikesButton.getStyle().set("border-radius", "12px"); // Adjust the value as needed

        // Set the strategy when each button is clicked
        singleBikeButton.addClickListener(event -> switchStrategy("Single Bike"));
        compareBikesButton.addClickListener(event -> switchStrategy("Compare Bikes"));

        // Add the buttons to a HorizontalLayout
        strategySelector = new HorizontalLayout(singleBikeButton, compareBikesButton);
        strategySelector.add(singleBikeButton, compareBikesButton);

        updateButton = new Button("Update Dashboard", event -> updateDashboard());
    }


    private void buildUI() {
        layout = new VerticalLayout(
                titleGroup,
                strategySelector,
                bikeChannelSelector,
                metricSelector,
                startDateTimePicker, startSecondSelector,
                endDateTimePicker, endSecondSelector,
                updateButton

        );

        layout.getElement().getThemeList().add(Material.DARK);
        splitLayout.addToPrimary(layout);
        setContent(splitLayout);

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

        LocalDateTime startTime = startDateTimePicker.getValue().withSecond(startSecondSelector.getValue());
        LocalDateTime endTime = endDateTimePicker.getValue().withSecond(endSecondSelector.getValue());

        String selectedMetric = metricSelector.getValue();
        if (selectedMetric == null) {
            Notification.show("Please select a metric.");
            return;
        }


        if (selectedChannel != null && startTime != null && endTime != null && selectedMetric != null) {

            controller.setMetricProcessor(selectedMetric, selectedChannel, startTime, endTime);
            List<AbstractDataProcessor.ProcessedData> results = controller.getResults();

            // Analyze the results to get sum and average
            DataAnalysisService.AnalysisResult analysisResult = dataAnalysisService.analyze(results);


            List<Component> components = context.buildView(results);
            VerticalLayout singleLayout = new VerticalLayout();
            singleLayout.add(components);
            layout.removeAll();
            buildUI();
            //layout.add(components);
            splitLayout.addToSecondary(singleLayout);
            //layout.add(components);
            // Create a new layout for the right side
            HorizontalLayout rightLayout = new HorizontalLayout();
            rightLayout.setWidth("100%");


                rightLayout.add(components);


            // Add the new layout to the main layout
            layout.add(rightLayout);


        } else {
            Notification.show("Please select a bike channel, time interval, and metric.");
        }



    }
}