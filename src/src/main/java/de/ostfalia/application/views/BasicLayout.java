package de.ostfalia.application.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import de.ostfalia.application.views.fahrrad.DashboardView;
import de.ostfalia.application.views.lampen.LampeView;
import de.ostfalia.application.views.talsperren.ListTalsperren;
import de.ostfalia.application.views.testecke.WebInhaltDynamisch;


public class BasicLayout extends AppLayout {

    H1 title = new H1("Bike Dashboard");


    public BasicLayout() {
        this.addToDrawer(createNav());

        DrawerToggle toggle = new DrawerToggle();

        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);
        addToNavbar(header);

    }


    public Component createNav() {
        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(new RouterLink("Talsperren", ListTalsperren.class));
        verticalLayout.add(new RouterLink("Testecke", WebInhaltDynamisch.class));
        verticalLayout.add(new RouterLink("Lampen", LampeView.class));
        verticalLayout.add(new RouterLink("Fahrrad", DashboardView.class));

        return verticalLayout;
    }

    public H1 getTitle() {
        return this.title;
    }


}

