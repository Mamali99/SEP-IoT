package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.data.fahrrad.strategies.impl.CompareBikesViewStrategy;
import de.ostfalia.application.data.fahrrad.strategies.impl.MetricViewStrategy;
import de.ostfalia.application.data.fahrrad.strategies.impl.SingleBikeViewStrategie;
import de.ostfalia.application.data.fahrrad.strategies.impl.TimeIntervalViewStrategy;
import de.ostfalia.application.views.BasicLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    private DashboardViewContext context;
    private BikeDashboardController controller;
    private ComboBox<String> strategySelector;
    private ComboBox<String> metricSelector;
    private Button updateButton;
    VerticalLayout layout;

    public DashboardView(BikeDashboardController bikeDashboardController) {
        this.controller = bikeDashboardController;
        this.context = new DashboardViewContext(new SingleBikeViewStrategie()); // Standardstrategie setzen
        //this.controller = new BikeDashboardController(context);
        // hier sagen wir dem Controller was wir wollen
        this.controller.setViewContext(context);
        this.controller.setDataProcessor("Speed");
        initializeComponents();
        buildUI();
    }

    private void initializeComponents() {
        // die Box mit dem Button
        strategySelector = new ComboBox<>("View Strategy");
        strategySelector.setItems("Single Bike", "Compare Bikes", "Metric", "Time Interval");
        // hier wird über Switch die neue Strategie gesetzt
        strategySelector.addValueChangeListener(event -> switchStrategy(event.getValue()));
        // Änderung der Strategy
        updateButton = new Button("Update Dashboard", event -> updateDashboard());

        // Kennzahlen Selektor
        metricSelector = new ComboBox<>("Metrics");
        metricSelector.setItems("Speed", "Distance", "Turns");
        metricSelector.addValueChangeListener(event -> switchMetric(event.getValue())); // Hier wird der Listener direkt zugewiesen
    }

    private void buildUI() {
        layout = new VerticalLayout(strategySelector, updateButton, metricSelector);
        layout.setSizeFull();
        setContent(layout); // Verwenden Sie setContent, um das Layout im BasicLayout zu setzen
    }

    private void rebuildUI(List<Component> viewComponents) {
        layout.removeAll(); // Löscht alle vorhandenen Komponenten im Hauptlayout
        buildUI();
        for (Component component : viewComponents) {
            layout.add(component); // Fügt die neuen Komponenten hinzu
        }

        setContent(layout); // Setzt das Hauptlayout als Inhalt der Seite
    }

    private void switchStrategy(String strategyName) {
        switch (strategyName) {
            case "Single Bike":
                context.setStrategy(new SingleBikeViewStrategie());
                break;
            case "Compare Bikes":
                context.setStrategy(new CompareBikesViewStrategy());
                break;
            case "Metric":
                context.setStrategy(new MetricViewStrategy());
                break;
            case "Time Interval":
                context.setStrategy(new TimeIntervalViewStrategy());
                break;
            default:
                throw new IllegalArgumentException("Unknown strategy");
        }
    }

    private void switchMetric(String value) {
        controller.setDataProcessor(value);
    }

    private void updateDashboard() {
        int channel = 1; // Beispielkanal
        LocalDateTime startTime = LocalDateTime.parse("2023-08-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        controller.updateDashboard(channel, startTime, endTime);
        List<AbstractDataProcessor.ProcessedData> processedDataList = controller.getDataProcessor().getResults();
        List<Component> viewComponents = context.buildView(processedDataList);

        //TO DO : remove xD ist das ok ?

        rebuildUI(viewComponents);

        // Rufen Sie buildUI in Ihrer DashboardView-Klasse auf
        // buildUI(viewComponents);
    }

}
