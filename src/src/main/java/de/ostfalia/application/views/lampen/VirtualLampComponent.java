package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.awt.*;

public class VirtualLampComponent extends Composite<Div> {
    private final Span statusLabel;
    private final Span intensityLvl;
    private final Icon lampIcon;
    private final Div lampBox;

    public VirtualLampComponent() {
        lampBox = new Div();
        lampBox.getStyle()
                .set("background-color", "rgba(0, 0, 0, 0.5)")
                .set("border-radius", "25px")
                .set("padding", "10px")
                .set("width", "250px")
                .set("height", "250px")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("box-shadow", "0px 0px 10px rgba(255, 255, 255, 0.1)")
                .set("position", "relative");

        lampIcon = new Icon(VaadinIcon.LIGHTBULB);
        lampIcon.setSize("100px");
        lampIcon.setColor("black");

        intensityLvl = new Span("Intensity: 0");
        intensityLvl.getStyle().set("font-size", "larger");

        statusLabel = new Span("The lamp is: OFF");
        statusLabel.getStyle().set("font-size", "larger");

        // Add the lamp icon inside the lamp box
        lampBox.add(lampIcon);

        VerticalLayout lampLayout = new VerticalLayout();
        lampLayout.add(lampBox, statusLabel, intensityLvl);
        lampLayout.setAlignItems(Alignment.CENTER);

        getContent().add(lampLayout);
    }

    public void updateLampState(boolean isOn, Color color, int intensity) {
        // Update lamp components based on the lamp state
        if (isOn) {
            statusLabel.setText("The lamp is: ON");
            lampIcon.setColor("white");
            lampBox.getStyle().set("background-color", "rgba(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.5)");
        } else {
            statusLabel.setText("The lamp is: OFF");
            lampIcon.setColor("black");
            lampBox.getStyle().set("background-color", "rgba(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.1)");
        }

        if (intensity >= 254) {
            intensityLvl.setText("Intensity: MAX (254)");
        } else {
            intensityLvl.setText("Intensity: " + intensity);
        }
    }
}
