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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    private final DashboardViewContext context;
    private final BikeDashboardController controller;

    private HorizontalLayout strategySelector;
    private Button updateButton;
    private ComboBox<Integer> bikeChannelSelector;
    private DateTimePicker startDateTimePicker;
    private DateTimePicker endDateTimePicker;
    private ComboBox<Integer> startSecondSelector;
    private ComboBox<Integer> endSecondSelector;
    private ListBox<String> metricSelector;

    private VerticalLayout zeitintervall;

    private TabSheet tabSheet;

    private TabSheet strategyTab;

    //Für Intervallgröße
    private NumberField intervalSizeField;
    private VerticalLayout layout;
    SplitLayout splitLayout;
    HorizontalLayout titleGroup;

    // Neue UI-Komponenten für die Dauer hinzufügen
    private NumberField durationValueField;
    private ComboBox<String> durationUnitSelector;

    @Autowired
    private DataAnalysisService dataAnalysisService;

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

        titleGroup = new HorizontalLayout();
        Icon dashicon = VaadinIcon.DASHBOARD.create();
        H2 title = new H2("Bike Dashboard");
        titleGroup.add(dashicon, title);


        // Bike Channels → Bikes werden angezeigt, wenn es Datensätze dafür gibt
        VerticalLayout bikeChannelOne = new VerticalLayout();
        bikeChannelSelector = new ComboBox<>("First Bike Channel");
        List<Integer> availableChannels = bikeService.getAvailableChannels();
        bikeChannelSelector.setItems(availableChannels);
        bikeChannelOne.add(bikeChannelSelector);
        bikeChannelOne.setVisible(true);


        ComboBox<Integer> bikeChannelSelectorOne = new ComboBox<>("First Bike Channel");
        bikeChannelSelectorOne.setItems(availableChannels);
        ComboBox<Integer> bikeChannelSelectorTwo = new ComboBox<>("Second Bike Channel");
        bikeChannelSelectorTwo.setItems(availableChannels);

        VerticalLayout bikeChannelTwo = new VerticalLayout();
        bikeChannelTwo.add(bikeChannelSelectorOne, bikeChannelSelectorTwo);

        // Metrics Selector
        metricSelector = new ListBox<>();
        metricSelector.setTooltipText("Kennzahlen für die Daten");
        metricSelector.setItems("Distance", "Rotation", "Speed", "Operating Time");
        metricSelector.setValue("Speed");
        metricSelector.addValueChangeListener(event -> updateMetricSelection(event.getValue()));


        // Zeitinervalanzeige
        zeitintervall = new VerticalLayout();
        VerticalLayout startEndZeitInterval = new VerticalLayout();

        // Start und End Time
        startDateTimePicker = new DateTimePicker("Start Time");
        startSecondSelector = new ComboBox<>("Start Second");
        startDateTimePicker.setStep(Duration.ofMinutes(1));
        startSecondSelector.setItems(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
        startSecondSelector.setValue(0);

        endDateTimePicker = new DateTimePicker("End Time");
        endDateTimePicker.setStep(Duration.ofMinutes(1));
        endSecondSelector = new ComboBox<>("End Second");
        endSecondSelector.setItems(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
        endSecondSelector.setValue(0);

        startEndZeitInterval.add(startDateTimePicker, startSecondSelector, endDateTimePicker, endSecondSelector);


        // Duration Intervall
        VerticalLayout durationIntervall = new VerticalLayout();
        durationValueField = new NumberField("Duration Value");
        durationValueField.setTooltipText("Dauer der Nutzung");
        durationUnitSelector = new ComboBox<>("Duration Unit", "Minutes", "Hours", "Days");
        durationUnitSelector.setValue("Minutes"); // Setze Standardwert
        durationIntervall.add(durationUnitSelector, durationValueField);


        // Initialisieren des neuen Feldes für die Intervallgröße
        intervalSizeField = new NumberField("Intervallgröße(in Minuten)");
        intervalSizeField.setValue(0.0); // Standardwert ist 0
        intervalSizeField.setMin(0);

        zeitintervall.add(startEndZeitInterval, durationIntervall);

        // Entscheidungslogik für Zeintintervall
        tabSheet = new TabSheet();
        tabSheet.add("Start und Endzeit", new Div(startEndZeitInterval));
        tabSheet.add("Dauer", new Div(durationIntervall));
        tabSheet.getSelectedTab().getStyle().set("color", "orange");


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

        // SingleBike or CompareBike Tab
        strategyTab = new TabSheet();
        strategyTab.add("Single Bike", bikeChannelOne);
        strategyTab.add("Compare Bikes", bikeChannelTwo);
        strategyTab.addSelectedChangeListener(event -> switchStrategy(event.getSelectedTab().getLabel()));
        bikeChannelOne.addClickListener(event -> switchStrategy("Single Bike"));
        bikeChannelTwo.addClickListener(event -> switchStrategy("Compare Bikes"));
        strategyTab.getSelectedTab().getStyle().set("color", "orange");

        updateButton = new Button("Update Dashboard", event -> updateDashboard());
        buildUI();
    }


    private void buildUI() {
        layout = new VerticalLayout(
                titleGroup,
                strategyTab,
                metricSelector,
                tabSheet,
                intervalSizeField,
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
            } else if ("Days".equals(durationUnit)) {
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
            VerticalLayout singleLayout = new VerticalLayout();
            singleLayout.add(components);
            layout.removeAll();
            buildUI();
            //layout.add(components);
            splitLayout.addToSecondary(singleLayout);
            //layout.add(components);
            // Create a new layout for the right side


        } else {
            Notification.show("No data available for the selected criteria.");
        }

    }
}