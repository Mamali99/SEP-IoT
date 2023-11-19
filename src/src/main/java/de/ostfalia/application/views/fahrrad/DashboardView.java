package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.material.Material;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.BasicLayout;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.views.fahrrad.strategies.impl.CompareBikesViewStrategy;
import de.ostfalia.application.views.fahrrad.strategies.impl.SingleBikeViewStrategie;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.componentfactory.PaperSlider;
import org.vaadin.addons.componentfactory.PaperSliderVariant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    private final DashboardViewContext context;
    private final BikeDashboardController controller;
    private Button updateButton;
    private ComboBox<Integer> bikeChannelSelector;
    private DateTimePicker startDateTimePicker;
    private DateTimePicker endDateTimePicker;
    private ListBox<String> metricSelector;
    private VerticalLayout zeitintervall;
    private TabSheet tabSheet;
    private TabSheet strategyTab;

    //Für Intervallgröße
    private Div intervalSliderValue;
    private PaperSlider intervalSizeField;
    private VerticalLayout layout;
    SplitLayout splitLayout;
    HorizontalLayout titleGroup;

    // Neue UI-Komponenten für die Dauer hinzufügen
    private PaperSlider durationValueField;
    private ComboBox<String> durationUnitSelector;
    private VerticalLayout bikeChannelOne;
    private VerticalLayout bikeChannelTwo;

    private VerticalLayout durationIntervall;
    private VerticalLayout startEndZeitInterval;

    private VerticalLayout verticalSlider;

    @Autowired
    public DashboardView(BikeDashboardController bikeDashboardController, DashboardViewContext dashboardViewContext, BikeService bikeService) {
        this.controller = bikeDashboardController;
        this.context = dashboardViewContext;

        // Default Stategy
        this.context.setStrategy(new SingleBikeViewStrategie());

        // Split Layer und Title
        splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(30);
        splitLayout.setSizeFull();

        buildTitleGroup();
        buildBikeChannels(bikeService);
        buildMetricsSelector();
        buildZeitintervall();
        buildDefaultValues();
        buildStrategyTab();
        buildUpdateButton();
        buildUI();
    }

    private void buildTitleGroup() {
        titleGroup = new HorizontalLayout();
        Icon dashicon = VaadinIcon.DASHBOARD.create();
        H2 title = new H2("Bike Dashboard");
        titleGroup.add(dashicon, title);
    }

    private void buildBikeChannels(BikeService bikeService) {
        List<Integer> availableChannels = bikeService.getAvailableChannels();

        bikeChannelOne = new VerticalLayout();
        bikeChannelSelector = new ComboBox<>("First Bike Channel");
        bikeChannelSelector.setItems(availableChannels);
        bikeChannelOne.add(bikeChannelSelector);
        bikeChannelOne.setVisible(true);

        ComboBox<Integer> bikeChannelSelectorOne = new ComboBox<>("First Bike Channel");
        bikeChannelSelectorOne.setItems(availableChannels);
        ComboBox<Integer> bikeChannelSelectorTwo = new ComboBox<>("Second Bike Channel");
        bikeChannelSelectorTwo.setItems(availableChannels);

        bikeChannelTwo = new VerticalLayout();
        bikeChannelTwo.add(bikeChannelSelectorOne, bikeChannelSelectorTwo);
    }

    private void buildMetricsSelector() {
        metricSelector = new ListBox<>();
        metricSelector.setItems("Distance", "Rotation", "Speed", "Operating time");
        metricSelector.setValue("Speed");
        //metricSelector.addValueChangeListener(event -> updateMetricSelection(event.getValue()));
    }

    private void buildZeitintervall() {
        zeitintervall = new VerticalLayout();

        // Initialize layout components
        buildStartEndZeitintervall();
        buildDurationIntervall();
        buildIntervalSizeInput();
        buildDurationSince();

        // Add components to the layout
        zeitintervall.add(startEndZeitInterval, durationIntervall);

        // Decision logic for time interval
        tabSheet = new TabSheet();
        tabSheet.add("Start and Endtime", new Div(startEndZeitInterval));
        tabSheet.add("Duration", new Div(durationIntervall));
        tabSheet.add("Duration Since", new Div(verticalSlider));
        tabSheet.getSelectedTab().getStyle().set("color", "green");
    }

    private void buildDurationSince() {
        verticalSlider = new VerticalLayout();
        PaperSlider slider = new PaperSlider();
        slider.setMin(1);
        slider.setMax(10);
        slider.setWidth("500px");
        slider.addValueChangeListener(event -> {
            Notification.show("Answer: " + event.getValue());
        });
        verticalSlider.add(slider);
    }

    private void buildStartEndZeitintervall() {
        startEndZeitInterval = new VerticalLayout();

        // Start und End Time
        startDateTimePicker = new DateTimePicker("Start Time");
        startDateTimePicker.setStep(Duration.ofSeconds(1)); // Set step to seconds for precise time selection

        endDateTimePicker = new DateTimePicker("End Time");
        endDateTimePicker.setStep(Duration.ofSeconds(1)); // Set step to seconds for precise time selection

        startEndZeitInterval.add(startDateTimePicker, endDateTimePicker);

        // Set default time values using buildDefaultValues() method
        buildDefaultValues();
    }

    private void buildDurationIntervall() {
        durationIntervall = new VerticalLayout();

        // Duration Intervall
        VerticalLayout sliderLine = new VerticalLayout();
        durationValueField = new PaperSlider("Duration Value");
        durationValueField.setTooltipText("Duration of Use");
        Div sliderValue = new Div();
        durationValueField.addValueChangeListener(e -> sliderValue.setText("Duration value: " + e.getValue()));
        durationValueField.setMax(60);
        durationValueField.setMin(0);
        durationValueField.setWidth("300px");
        durationValueField.setMaxMarkers(6);
        durationValueField.setPinned(true);
        durationValueField.setSnaps(true);
        durationValueField.addThemeVariants(PaperSliderVariant.LUMO_SECONDARY);

        sliderLine.add(durationValueField, sliderValue);

        durationUnitSelector = new ComboBox<>("Duration Unit", "Minutes", "Hours", "Days");
        durationUnitSelector.setValue("Minutes"); // Set default value
        durationIntervall.add(durationUnitSelector, sliderLine);
    }

    private void buildIntervalSizeInput() {

        VerticalLayout intervalVertical = new VerticalLayout();
        intervalSizeField = new PaperSlider("Interval (in Minutes)");
        intervalSizeField.setTooltipText("How long are the intervals between data");
        intervalSliderValue = new Div();
        intervalSliderValue.setText("Interval not specified.");
        intervalSizeField.addValueChangeListener(event -> intervalSliderValue.setText("Interval size: " + (event.getValue() + 1)));
        intervalSizeField.setMax(60);
        intervalSizeField.setMin(0);
        intervalSizeField.setValue(0);
        intervalSizeField.setWidth("300px");
        intervalSizeField.setMaxMarkers(6);
        intervalSizeField.setPinned(true);
        intervalSizeField.setSnaps(true);
        intervalSizeField.addThemeVariants(PaperSliderVariant.LUMO_SECONDARY);
        intervalVertical.add(intervalSizeField, intervalSliderValue);

        // Initialize the new field for the interval size
        //intervalSizeField = new NumberField("Interval (in Minutes)");
        //intervalSizeField.setValue(0.0); // Default value is 0
        //intervalSizeField.setMin(0);
        zeitintervall.add(intervalVertical);

    }

    private void buildDefaultValues() {
        // Festlegen der Standardwerte für das Start- und Enddatum/-zeit
        LocalDateTime defaultStartTime = LocalDateTime.of(2023, 9, 8, 16, 8, 0); // 8. September 2023, 16:08:01
        LocalDateTime defaultEndTime = LocalDateTime.of(2023, 9, 8, 16, 9, 0); // 8. September 2023, 16:08:31
        startDateTimePicker.setValue(defaultStartTime.minusSeconds(defaultStartTime.getSecond()));
        endDateTimePicker.setValue(defaultEndTime.minusSeconds(defaultEndTime.getSecond()));

        // Festlegen des Standardkanals und der Standardmetrik
        bikeChannelSelector.setValue(1); // Standardkanal 1
        metricSelector.setValue("Distance"); // Standardmetrik "Distance"
    }

    private void buildStrategyTab() {
        // SingleBike or CompareBike Tab
        strategyTab = new TabSheet();
        strategyTab.add("Single Bike", bikeChannelOne);
        strategyTab.add("Compare Bikes", bikeChannelTwo);
        strategyTab.addSelectedChangeListener(event -> switchStrategy(event.getSelectedTab().getLabel()));
        bikeChannelOne.addClickListener(event -> switchStrategy("Single Bike"));
        bikeChannelTwo.addClickListener(event -> switchStrategy("Compare Bikes"));
        strategyTab.getSelectedTab().getStyle().set("color", "green");
    }

    private void buildUpdateButton() {
        updateButton = new Button("Update Dashboard", event -> updateDashboardOnChange());
    }

    private void buildUI() {
        layout = new VerticalLayout(
                titleGroup,
                strategyTab,
                metricSelector,
                tabSheet,
                zeitintervall,
                updateButton
        );


        layout.getElement().getThemeList().add(Material.DARK);
        splitLayout.addToPrimary(layout);
        setContent(splitLayout);
    }

    private void updateDashboardOnChange() {
        // Call the updateDashboard function
        updateDashboard();
        // Display a notification
        Notification notification = Notification
                .show("Dashboard Updated!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_START);

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


    private void updateDashboard() {
        Integer selectedChannel = bikeChannelSelector.getValue();
        String selectedMetric = metricSelector.getValue();
        int intervalSizeInMinutes = intervalSizeField.getValue();

        if (selectedMetric == null) {
            Notification.show("Please select a metric.");
            return;
        }

        List<AbstractDataProcessor.ProcessedData> results;

        // If duration tab is selected
        if (tabSheet.getSelectedTab().getLabel().equals("Duration")) {
            Duration duration = null;
            if (durationValueField != null && durationValueField.getValue() != null) {
                long durationValue = durationValueField.getValue().longValue();
                String durationUnit = durationUnitSelector.getValue();
                if ("Minutes".equals(durationUnit)) {
                    duration = Duration.ofMinutes(durationValue);
                } else if ("Hours".equals(durationUnit)) {
                    duration = Duration.ofHours(durationValue);
                } else if ("Days".equals(durationUnit)) {
                    duration = Duration.ofDays(durationValue);
                }
            }

            if (selectedChannel != null && duration != null) {
                controller.setMetricProcessor(selectedMetric, selectedChannel, duration, intervalSizeInMinutes);
                results = controller.getResults();
            } else {
                Notification.show("Please select a bike channel and a duration.");
                return;
            }
        } else { // "Start und Endzeit" tab is selected
            LocalDateTime startTime = startDateTimePicker.getValue();
            LocalDateTime endTime = endDateTimePicker.getValue();
            if (selectedChannel != null && startTime != null && endTime != null) {
                controller.setMetricProcessor(selectedMetric, selectedChannel, startTime, endTime, intervalSizeInMinutes);
                results = controller.getResults();
            } else {
                Notification.show("Please select a bike channel and a time interval.");
                return;
            }
        }

        if (results != null && !results.isEmpty()) {
            List<Component> components = context.buildView(results);
            VerticalLayout singleLayout = new VerticalLayout();
            singleLayout.add(components);
            layout.removeAll();
            buildUI();
            splitLayout.addToSecondary(singleLayout);
        } else {
            Notification.show("No data available for the selected criteria.");
        }


    }
}