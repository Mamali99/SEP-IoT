package de.ostfalia.application.views.fahrreader;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.views.BasicLayout;
import de.ostfalia.application.views.fahrreader.strategy.FahrradStrategy;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;

@Route("/SE/BikeDashboard")

public class FahrradView extends BasicLayout {

    FahrradStrategy fahrradStrategy;

    // jetzt ist Single bei default inject → muss dan zu Laufzeit neuer Bean injiziert werden
    // mit einem Setter oder irgendwann
    public FahrradView(@Qualifier("singleFahrradStrategy") FahrradStrategy fahrradStrategy) throws IOException {
        this.fahrradStrategy = fahrradStrategy;
        //Titel
        Hr hr = new Hr();
        Hr hr2 = new Hr();

        // Name mit Icon - Horizontal
        HorizontalLayout layoutRow = new HorizontalLayout();
        Icon icon = VaadinIcon.STEP_BACKWARD.create();
        H4 nameLabel = new H4("Biky Biky");


        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.add(hr);
        layoutRow.add(icon);
        layoutRow.add(nameLabel);
        pageLayout.add(layoutRow);
        pageLayout.add(hr2);


        this.setContent(pageLayout);
        this.getTitle().setText("Bike Dashboard Test");
    }


}