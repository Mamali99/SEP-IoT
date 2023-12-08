package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.commandImp.BlinkCommand;
import de.ostfalia.application.data.lamp.commandImp.TurnOffCommand;
import de.ostfalia.application.data.lamp.commandImp.TurnOnCommand;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.views.BasicLayout;
import org.vaadin.addons.tatu.ColorPicker;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@Route("/SE/LightAdapter")

public class LampeView extends BasicLayout {

    private RemoteController remoteController;




    public LampeView(RemoteController remoteController) throws IOException {

        this.remoteController = remoteController;
        Button turnOnButton = new Button("Turn On", e -> turnOnLamp());
        Button turnOffButton = new Button("Turn Off", e -> turnOffLamp());
        Button blinkButton = new Button("Blink", e -> blinkLamp());


        // Füge die Buttons zum Layout hinzu
        VerticalLayout layout = new VerticalLayout();
        layout.add(turnOnButton, turnOffButton, blinkButton);

        // Setze das Layout als Inhalt der View
        this.setContent(layout);

    }

    private void turnOffLamp() {
        try {
            Command turnOffCommand = new TurnOffCommand(new Java2NodeRedLampAdapter());
            remoteController.executeCommand(turnOffCommand);

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    // Funktionen
    private void turnOnLamp() {
        try {
            // Erstelle und führe TurnOnCommand aus
            Command turnOnCommand = new TurnOnCommand(new Java2NodeRedLampAdapter());
            remoteController.executeCommand(turnOnCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void blinkLamp() {
        try {
            // Erstelle und führe BlinkCommand aus
            Command blinkCommand = new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000);
            remoteController.executeCommand(blinkCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }

    private String rgb2Hex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }


    private int mapPercentageToInt(int percentage) {
        return (int) Math.round(percentage * 2.54); // Umrechnung von Prozent in den Bereich von 0 bis 254
    }

    private int mapIntToPercentage(float intensity) {
        return (int) (intensity / 254) * 100;
    }

    private String getStateAsString() throws IOException {
        return null;

    }


    private void changeColor(Color color) throws IOException {


    }

    private void changeIntensity(float intensity) throws IOException {

    }

    private void enableNameChange() {


    }

    private void turnOnOffWithSettings(String value) throws IOException {


    }

    private void switchState() throws IOException {

    }

    private void switchStateWithIntensity() throws IOException {

    }

    private void switchStateWithColor() throws IOException {


    }


}
