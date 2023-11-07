package de.ostfalia.application.views.fahrrad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.fahrrad.controller.BikeDashboardController;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.fahrrad.strategies.DashboardViewContext;
import de.ostfalia.application.data.fahrrad.strategies.impl.CompareBikesViewStrategy;
import de.ostfalia.application.data.fahrrad.strategies.impl.MetricViewStrategy;
import de.ostfalia.application.data.fahrrad.strategies.impl.SingleBikeViewStrategie;
import de.ostfalia.application.data.fahrrad.strategies.impl.TimeIntervalViewStrategy;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Route("/SE/BikeDashboard")
public class DashboardView extends BasicLayout {

    private DashboardViewContext context;


    private BikeDashboardController controller;
    @Autowired
    private BikeService bikeService;
    private ComboBox<String> strategySelector;
    private Button updateButton;

    public DashboardView(BikeDashboardController bikeDashboardController) {
       this.controller = bikeDashboardController;
        this.context = new DashboardViewContext(new SingleBikeViewStrategie()); // Standardstrategie setzen
        //this.controller = new BikeDashboardController(context);
        this.controller.setViewContext(context);
        this.controller.setDataProcessor(new SpeedDataProcessor(bikeService));
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

    private void updateDashboard() {
        controller.updateDashboard();
        List<AbstractDataProcessor.ProcessedData> processedDataList = controller.getDataProcessor().getResults();
        context.buildView(processedDataList);
        // Hier würden Sie die UI-Komponenten aktualisieren, um die neuen Daten anzuzeigen
    }
}
