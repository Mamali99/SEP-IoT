package de.ostfalia.application.views.testecke;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.views.BasicLayout;

import java.io.IOException;

@Route("/dynamisch")
public class WebInhaltDynamisch extends BasicLayout {
    private VerticalLayout colorfulBox = new VerticalLayout();
    LampController adapter;
    public WebInhaltDynamisch(LampController lamp) {
        this.adapter = lamp;
        TextField field = new TextField();
        Button checkStateButton = new Button("Check Lamp State");
        checkStateButton.addClickListener(e  -> showLampState());
        HorizontalLayout pageLayout = new HorizontalLayout();
        pageLayout.add(checkStateButton);
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

    private void showLampState() {
        boolean lampState;
        String lampName;
        try {
            lampState = adapter.getState();
            lampName = adapter.getName();
            if (lampState) {
                System.out.println("Prüfe ob die Lampe an ist..");
                System.out.println("Lamp Name: " + lampName);
            } else {
                System.out.println("Lamp Namezzz: " + lampName);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while getting the lamp state: " + e.getMessage());
        }
    }




    private Div getSmallBox() {
        Div smallBox = new Div();
        smallBox.setHeight("50px");
        smallBox.setWidth("50px");
        smallBox.getStyle().set("border-radius", "5px");
        return smallBox;
    }

    private void addGreen() {
        Div smallbox = getSmallBox();
        smallbox.getStyle().set("background-color", "#00FF00");
        colorfulBox.add(smallbox);
    }

    private void addRed() {
        Div smallbox = getSmallBox();
        smallbox.getStyle().set("background-color", "#FF0000");
        colorfulBox.add(smallbox);
    }

    private void clearBox() {
        this.colorfulBox.removeAll();
    }


}


