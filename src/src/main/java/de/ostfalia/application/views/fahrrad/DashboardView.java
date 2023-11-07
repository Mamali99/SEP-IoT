package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;
import de.ostfalia.application.data.fahrrad.impl.RotationDataProcessor;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.views.fahrrad.strategies.impl.CompareBikesViewStrategy;
import de.ostfalia.application.views.fahrrad.strategies.impl.MetricViewStrategy;
import de.ostfalia.application.views.fahrrad.strategies.impl.SingleBikeViewStrategie;
import de.ostfalia.application.views.fahrrad.strategies.impl.TimeIntervalViewStrategy;
import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    @Autowired
    private DashboardViewContext context;

    @Autowired
    private BikeDashboardController controller;

    @Autowired
    private BikeService bikeService;

    private ComboBox<String> strategySelector;
    private Button updateButton;

    public DashboardView(BikeDashboardController bikeDashboardController, DashboardViewContext dashboardViewContext) {
        this.controller = bikeDashboardController;
        this.context = dashboardViewContext;
        this.context.setStrategy(new SingleBikeViewStrategie());
        initializeComponents();
        buildUI();
    }

    private void initializeComponents() {
        strategySelector = new ComboBox<>("View Strategy");
        strategySelector.setItems("Single Bike", "Compare Bikes", "Metric", "Time Interval");
        strategySelector.addValueChangeListener(event -> switchStrategy(event.getValue()));
        updateButton = new Button("Update Dashboard", event -> updateDashboard());
    }

    private void buildUI() {
       VerticalLayout layout = new VerticalLayout(strategySelector, updateButton);
        layout.setSizeFull();
        setContent(layout); // Verwenden Sie setContent, um das Layout im BasicLayout zu setzen

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

    // Diese Methode sollte Teil der DashboardView-Klasse sein
    private void initializeController(String strategyName) {
        // Entscheide, welcher DataProcessor verwendet werden soll, basierend auf der Strategie
        AbstractDataProcessor processor;
        switch (strategyName) {
            case "Single Bike":
                processor = new SpeedDataProcessor(bikeService); // oder eine entsprechende Instanz
                break;
            case "Compare Bikes":
                processor = new RotationDataProcessor(); // oder eine entsprechende Instanz
                break;
            // Fügen Sie hier weitere Fälle hinzu
            default:
                throw new IllegalArgumentException("Unknown strategy");
        }
        controller.setDataProcessor(processor);
        // Führen Sie hier weitere Initialisierungen durch, falls nötig
    }


    private void updateDashboard() {

    }
}
