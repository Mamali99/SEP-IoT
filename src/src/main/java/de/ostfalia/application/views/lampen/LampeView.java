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
import de.ostfalia.application.data.lamp.lampController.LampController;
import de.ostfalia.application.views.BasicLayout;
import org.vaadin.addons.tatu.ColorPicker;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@Route("/SE/LightAdapter")

public class LampeView extends BasicLayout {

    private final LampController lampController;
    private String onOffEinstellung;
    private int intensity;
    private Color awtColor;
    private H4 nameLabel;
    private TextField nameField;
    private TextField stateField;
    private final Button onOffButton;
    private Icon icon;
    private final Button nameButton;

    // test

    public LampeView(LampController lampController) throws IOException {

        this.lampController = lampController;

        //Titel
        Hr hr = new Hr();
        Hr hr2 = new Hr();

        // Name mit Icon - Horizontal
        HorizontalLayout layoutRow = new HorizontalLayout();
        icon = VaadinIcon.LIGHTBULB.create();
        nameLabel = new H4();
        // Rest der GUI

        // Zustand
        stateField = new TextField("Lampenzustand");
        stateField.setReadOnly(true);
        stateField.setValue(getStateAsString());

        // Intensität
        IntegerField integerField = new IntegerField();
        integerField.setLabel("Intensität");
        integerField.setHelperText("Die aktuelle Intensität");
        integerField.setMin(0);
        integerField.setMax(100);
        integerField.setStep(5);
        integerField.setValue(50);
        // soll nacher mit getter gemacht werden
        intensity = integerField.getValue();

        HtmlComponent suffix = new HtmlComponent("div");
        suffix.getElement().setText("%");
        integerField.setSuffixComponent(suffix);
        integerField.setStepButtonsVisible(true);

        integerField.addValueChangeListener(event -> {
            intensity = event.getValue();
            int intensityValue = mapPercentageToInt(intensity);
            // can set hier benutzen mit INT
        });


        // Color Picker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbauswahl");
        colorPicker
                .setPresets(Arrays.asList(
                        new ColorPicker.ColorPreset("#6499E9", "Blau"),
                        new ColorPicker.ColorPreset("#FF4B91", "Pink"),
                        new ColorPicker.ColorPreset("#F99417", "Orange"),
                        new ColorPicker.ColorPreset("#FFCF96", "Warmes Gelb")

                ));

        colorPicker.setHelperText("Hier können Sie eine Farbe auswählen");
        colorPicker.setValue("#FFCF96");

        colorPicker.addValueChangeListener(event -> {
            String hexColor = event.getValue();
            awtColor = hex2Rgb(hexColor);
        });


        // ON/OFF Button
        onOffButton = new Button("ON/OFF", e -> {
            try {
                turnOnOffWithSettings(onOffEinstellung);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        // Zusatzeinstellung für ON/OFF
        // bei change sollte nut die eintellung set
        // wenn on off soll dan geprüft werden welches setting

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Zusatzeinstellung zum Einschalten");
        radioGroup.setItems("Mit Intensität", "Mit Farbe", "Ohne Zusatzangaben");
        radioGroup.setValue("Ohne Zusatzangaben");
        onOffEinstellung = radioGroup.getValue();

        radioGroup.addValidationStatusChangeListener(event -> {
            onOffEinstellung = event.getSource().getValue();
        });


        // Name Ändern
        nameField = new TextField("Namen Ändern");
        nameField.setReadOnly(true);
        nameButton = new Button("Ändern", e -> enableNameChange());
        nameField.addValueChangeListener(event -> {
            String text = event.getValue();
            try {
                lampController.setName(text);
                String name = lampController.getName();
                System.out.println("NAME ISIT" + name);
                nameLabel.setText(lampController.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });


        icon.getElement().setAttribute("icon", "vaadin:lightbulb");
        nameLabel.setText(lampController.getName());
        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.add(hr);
        layoutRow.add(icon);
        layoutRow.add(nameLabel);
        pageLayout.add(layoutRow);
        pageLayout.add(hr2);
        pageLayout.add(stateField);
        pageLayout.add(integerField);
        pageLayout.add(colorPicker);
        pageLayout.add(radioGroup);
        pageLayout.add(onOffButton);
        pageLayout.add(nameField);
        pageLayout.add(nameButton);

        this.setContent(pageLayout);
        this.getTitle().setText("Lampensteuerung");
    }
    // Funktionen

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }

    private int mapPercentageToInt(int percentage) {
        return (int) Math.round(percentage * 2.54); // Umrechnung von Prozent in den Bereich von 0 bis 254
    }

    private String getStateAsString() throws IOException {
        if (lampController.getState()) {
            return "Angeschaltet";
        } else {
            return "Ausgeschaltet";
        }
    }

    private void enableNameChange() {
        if (nameField.isReadOnly()) {
            nameField.setReadOnly(false);
            nameButton.setText("Speichern");
        } else {
            nameField.setReadOnly(true);
            nameButton.setText("Ändern");
        }


    }

    private void turnOnOffWithSettings(String value) throws IOException {

        if (lampController.getState()) {
            lampController.switchOff();
            stateField.setValue(getStateAsString());
            onOffButton.setText("On");
            icon.setColor("black");
        } else {
            if (value.equals("Mit Intensität")) {
                switchStateWithIntensity();
            } else if (value.equals("Mit Farbe")) {
                switchStateWithColor();
            } else {
                switchState();
            }
        }

    }

    private void switchState() throws IOException {

        lampController.switchOn();
        stateField.setValue(getStateAsString());
        onOffButton.setText("Off");
        icon.setColor("orange");

    }

    private void switchStateWithIntensity() throws IOException {

        lampController.switchOn(intensity);
        stateField.setValue(getStateAsString());
        onOffButton.setText("Off");
        icon.setColor("orange");

    }

    private void switchStateWithColor() throws IOException {

        lampController.switchOn(awtColor);
        stateField.setValue(getStateAsString());
        onOffButton.setText("Off");
        icon.setColor("orange");
    }


}
