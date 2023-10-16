package de.ostfalia.application.views.testecke;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.views.BasicLayout;

@Route("/dynamisch")
public class WebInhaltDynamisch extends BasicLayout {
    private VerticalLayout colorfulBox = new VerticalLayout();
    public WebInhaltDynamisch(){
        HorizontalLayout pageLayout = new HorizontalLayout();
        pageLayout.add(colorfulBox);
        // gruene box hinzufuegen
        Button btnGreen = new Button("gruen hinzufuegen");
        btnGreen.addClickListener(buttonClickEvent -> addGreen());
        pageLayout.add(btnGreen);

        // rote box hinzufuegen
        Button btnRed = new Button("rot hinzufuegen");
        btnRed.addClickListener(buttonClickEvent -> addRed());
        pageLayout.add(btnRed);

        // clear btn
        Button clear = new Button("loeschen");
        clear.addClickListener(buttonClickEvent -> clearBox());
        pageLayout.add(clear);

        this.setContent(pageLayout);

        this.getTitle().setText("Webinhalt dynamisch");
    }
    private Div getSmallBox(){
        Div smallBox = new Div();
        smallBox.setHeight("50px");
        smallBox.setWidth("50px");
        smallBox.getStyle().set("border-radius", "5px");
        return smallBox;
    }
    private void addGreen(){
        Div smallbox = getSmallBox();
        smallbox.getStyle().set("background-color", "#00FF00");
        colorfulBox.add(smallbox);
    }
    private void addRed(){
        Div smallbox = getSmallBox();
        smallbox.getStyle().set("background-color", "#FF0000");
        colorfulBox.add(smallbox);
    }
    private void clearBox(){
        this.colorfulBox.removeAll();
    }
}

