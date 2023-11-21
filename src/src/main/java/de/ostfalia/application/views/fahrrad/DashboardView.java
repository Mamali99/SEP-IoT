package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.component.select.Select;
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
import java.util.ArrayList;
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
    private Div intervalSliderValue;
    private PaperSlider intervalSizeField;

    private Select<String> durationTypeSelect;
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

    // Checkbox to enable or disable data smoothing
    private Checkbox smoothDataCheckbox;

    // Compare Bike
    ComboBox<Integer> bikeChannelSelectorOne;
    ComboBox<Integer> bikeChannelSelectorTwo;

    @Autowired
    public DashboardView(BikeDashboardController bikeDashboardController, DashboardViewContext dashboardViewContext, BikeService bikeService) {
        this.controller = bikeDashboardController;
        this.context = dashboardViewContext;
        // Default Strategy
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
        buildSmoothingOption();
        buildUI();


    }


    private void buildTitleGroup() {
        titleGroup = new HorizontalLayout();
        Icon dashicon = VaadinIcon.DASHBOARD.create();
        dashicon.setSize("30px");
        H2 title = new H2("Bike Dashboard");
        titleGroup.add(dashicon, title);
    }

    private void buildSmoothingOption() {
        smoothDataCheckbox = new Checkbox("Smooth Data");
    }


    private void buildBikeChannels(BikeService bikeService) {
        List<Integer> availableChannels = bikeService.getAvailableChannels();

        bikeChannelOne = new VerticalLayout();
        bikeChannelSelector = new ComboBox<>("First Bike Channel");
        bikeChannelSelector.setItems(availableChannels);
        bikeChannelOne.add(bikeChannelSelector);
        bikeChannelOne.setVisible(true);

        bikeChannelSelectorOne = new ComboBox<>("First Bike Channel");
        bikeChannelSelectorOne.setItems(availableChannels);
        bikeChannelSelectorTwo = new ComboBox<>("Second Bike Channel");
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

        // Add components to the layout
        zeitintervall.add(startEndZeitInterval, durationIntervall);

        // Decision logic for time interval
        tabSheet = new TabSheet();
        tabSheet.add("Start and Endtime", new Div(startEndZeitInterval));
        tabSheet.add("Duration", new Div(durationIntervall));
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

        // Duration Type ComboBox
        durationTypeSelect = new Select<>();
        durationTypeSelect.setLabel("Interval Duration Type");
        durationTypeSelect.setItems("Seconds", "Minutes", "Hours", "Days");
        durationTypeSelect.setValue("Minutes"); // Set default value

        // Interval Size PaperSlider
        intervalSizeField = new PaperSlider("Interval Size");
        intervalSizeField.setTooltipText("How long are the intervals between data");
        intervalSliderValue = new Div();
        intervalSliderValue.setText("Automatic Interval Calculation");
        intervalSizeField.addValueChangeListener(event -> {
            if (event.getValue() == 0) {
                intervalSliderValue.setText("Automatic Interval Calculation");
            } else {
                intervalSliderValue.setText("Interval size: " + event.getValue());
            }
        });
        intervalSizeField.setMax(60);
        intervalSizeField.setMin(0);
        intervalSizeField.setValue(0);
        intervalSizeField.setWidth("300px");
        intervalSizeField.setMaxMarkers(6);
        intervalSizeField.setPinned(true);
        intervalSizeField.setSnaps(true);
        intervalSizeField.addThemeVariants(PaperSliderVariant.LUMO_SECONDARY);

        // Add components to the layout
        intervalVertical.add(durationTypeSelect, intervalSizeField, intervalSliderValue);
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
    }

    private void buildUpdateButton() {
        updateButton = new Button("Update Dashboard", event -> updateDashboardOnChange());
    }

    private void buildUI() {
        layout = new VerticalLayout(
                titleGroup,
                strategyTab,
                metricSelector,
                smoothDataCheckbox,
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
        //Single
        Integer selectedChannel = bikeChannelSelector.getValue();
        // Compare
        Integer compareChannelSelectorOne = bikeChannelSelectorOne.getValue();
        Integer compareChannelSelectorTwo = bikeChannelSelectorTwo.getValue();

        String selectedMetric = metricSelector.getValue();
        // Interval
        double intervalValue = intervalSizeField.getValue();
        String durationType = durationTypeSelect.getValue();
        int intervalSizeInSeconds = convertToSeconds(durationType, intervalValue);

        String currentStrategy = strategyTab.getSelectedTab().getLabel();
        boolean smoothingData = smoothDataCheckbox.getValue();

        if (selectedMetric == null) {
            Notification.show("Please select a metric.");
            return;
        }


        List<AbstractDataProcessor.ProcessedData> results;

        // If duration tab is selected
        if (tabSheet.getSelectedTab().getLabel().equals("Duration")) {

            // Validate if the selected duration is not shorter than the interval size
            Duration selectedDuration = getDuration();
            if (selectedDuration != null && selectedDuration.getSeconds() < intervalSizeInSeconds) {
                Notification notification = new Notification("Selected duration is shorter than the interval size. Please adjust your selection.", 3000, Notification.Position.BOTTOM_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                return;
            }
            if (currentStrategy.equals("Single Bike")) {
                results = processDurationData(selectedChannel, intervalSizeInSeconds, selectedMetric, smoothingData);
            } else {
                List<AbstractDataProcessor.ProcessedData> result1 = processDurationData(compareChannelSelectorOne, intervalSizeInSeconds, selectedMetric, smoothingData);
                List<AbstractDataProcessor.ProcessedData> result2 = processDurationData(compareChannelSelectorTwo, intervalSizeInSeconds, selectedMetric, smoothingData);

                // Merge the results
                List<AbstractDataProcessor.ProcessedData> mergedResults = new ArrayList<>(result1);
                mergedResults.addAll(result2);
                results = mergedResults;

            }


        } else { // "Start und Endzeit" tab is selected
            LocalDateTime startTime = startDateTimePicker.getValue();
            LocalDateTime endTime = endDateTimePicker.getValue();

            if (startTime != null && endTime != null && startTime.plusSeconds(intervalSizeInSeconds).isAfter(endTime)) {
                Notification notification = new Notification("Selected duration is shorter than the interval size. Please adjust your selection.", 3000, Notification.Position.BOTTOM_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                return;
            }


            if (currentStrategy.equals("Single Bike")) {
                results = processStartEndData(selectedChannel, intervalSizeInSeconds, startTime, endTime, selectedMetric, smoothingData);
            } else {
                List<AbstractDataProcessor.ProcessedData> result1 = processStartEndData(compareChannelSelectorOne, intervalSizeInSeconds, startTime, endTime, selectedMetric, smoothingData);

                // Merge the results
                List<AbstractDataProcessor.ProcessedData> result2 = processStartEndData(compareChannelSelectorTwo, intervalSizeInSeconds, startTime, endTime, selectedMetric, smoothingData);
                List<AbstractDataProcessor.ProcessedData> mergedResults = new ArrayList<>(result1);
                mergedResults.addAll(result2);
                results = mergedResults;

            }
        }

        // hier kommt duration since


        if (results == null || results.isEmpty()) {
            Notification.show("No data available for the selected criteria.");
        } else {
            List<Component> components = context.buildView(results);
            VerticalLayout singleLayout = new VerticalLayout();
            singleLayout.add(components);
            layout.removeAll();
            buildUI();
            splitLayout.addToSecondary(singleLayout);
        }


    }

    public List<AbstractDataProcessor.ProcessedData> processDurationData(Integer selectedChannel, int intervalSizeInMinutes, String selectedMetric, boolean smoothingData) {
        Duration duration = getDuration();
        if (selectedChannel != null && duration != null) {
            controller.setMetricProcessor(selectedMetric);
            controller.setShouldSmoothData(smoothingData);
            controller.updateDashboard(selectedChannel, duration, intervalSizeInMinutes);
            return controller.getResults();
        } else {
            Notification.show("Please select a bike channel and a duration.");
            return null;
        }
    }

    public List<AbstractDataProcessor.ProcessedData> processStartEndData(Integer selectedChannel, int intervalSizeInMinutes, LocalDateTime startTime, LocalDateTime endTime, String selectedMetric, boolean smoothingData) {


        if (selectedChannel != null && startTime != null && endTime != null) {
            controller.setMetricProcessor(selectedMetric);
            controller.setShouldSmoothData(smoothingData);
            controller.updateDashboard(selectedChannel, startTime, endTime, intervalSizeInMinutes);
            return controller.getResults();
        } else {
            Notification.show("Please select a bike channel and a start and end time.");
            return null;
        }
    }

    private Duration getDuration() {
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
        return duration;
    }

    private int convertToSeconds(String durationType, double intervalValue) {
        return switch (durationType) {
            case "Seconds" -> (int) intervalValue;
            case "Minutes" -> (int) (intervalValue * 60); // convert minutes to seconds
            case "Hours" -> (int) (intervalValue * 60 * 60); // convert hours to seconds
            case "Days" -> (int) (intervalValue * 24 * 60 * 60); // convert days to seconds
            default -> throw new IllegalArgumentException("Unknown duration type: " + durationType);
        };
    }

}