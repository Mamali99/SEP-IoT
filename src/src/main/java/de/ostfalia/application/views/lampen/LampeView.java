package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

    Color awtColor;

    private H4 nameLabel;
    private TextField nameField;
    private TextField stateField;
    private final Button onOffButton;
    private Icon icon;
    private final Button nameButton;
    public LampeView(LampController lampController) throws IOException {

        this.lampController = lampController;

        //Titel
        Hr hr = new Hr();
        H2 h2 = new H2();
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

        HtmlComponent suffix = new HtmlComponent("div");
        suffix.getElement().setText("%");
        integerField.setSuffixComponent(suffix);

        // mit getter hohlen
        integerField.setValue(25);
        integerField.setStepButtonsVisible(true);

        integerField.addValueChangeListener(event -> {
            int percentageValue = event.getValue();
            int intensityValue = mapPercentageToInt(percentageValue);
            // can set hier benutzen mit INT
        });



        // Color Picker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbauswahl");
        colorPicker
                .setPresets(Arrays.asList(new ColorPicker.ColorPreset("#00ff00", "Color 1"),
                        new ColorPicker.ColorPreset("#ff0000", "Color 2")));

        colorPicker.setTooltipText("Hier können Sie eine Farbe auswählen");

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
        radioGroup.setLabel("Zusatzeinstellung zum Ein- und Ausschalten");
        radioGroup.setItems("Mit Intensität", "Mit Farbe", "Ohne Zusatzangaben");
        radioGroup.setValue("Ohne Zusatzangaben");
        onOffEinstellung = radioGroup.getValue();

        radioGroup.addValidationStatusChangeListener(event -> {
           onOffEinstellung =  event.getSource().getValue();
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


        h2.setText("Lampensteuerung");
        icon.getElement().setAttribute("icon", "vaadin:lightbulb");
        nameLabel.setText(lampController.getName());
        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.add(h2);
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
        if(lampController.getState()){
            return "Angeschaltet";
        } else {
            return "Ausgeschaltet";
        }
    }

    private void enableNameChange() {
        if(nameField.isReadOnly()){
            nameField.setReadOnly(false);
            nameButton.setText("Speichern");
        } else {
            nameField.setReadOnly(true);
            nameButton.setText("Ändern");
        }


    }

    private void turnOnOffWithSettings(String value) throws IOException {
        if (value.equals("Mit Intensität")) {
            // Rufe Methode für Intensität auf
            //braucht get Intensity
            switchStateWithIntensity();
        } else if (value.equals("Mit Farbe")) {
            //braucht get Color
            // Rufe Methode für Farbe auf
            switchStateWithColor();
        } else {
            // Rufe Methode ohne Zusatzangaben auf
            switchState();
        }

    }

    private void switchState() throws IOException {
        if (lampController.getState()) {
            lampController.switchOff();
            stateField.setValue(getStateAsString());
            onOffButton.setText("On");
            icon.setColor("black");

        } else {
            lampController.switchOn();
            stateField.setValue(getStateAsString());
            onOffButton.setText("Off");
            icon.setColor("orange");
        }

    }
    private void switchStateWithIntensity() throws IOException {
        if (lampController.getState()) {
            lampController.switchOff();
            stateField.setValue(getStateAsString());
            onOffButton.setText("On");
            icon.setColor("black");

        } else {
            lampController.switchOn();
            stateField.setValue(getStateAsString());
            onOffButton.setText("Off");
            icon.setColor("orange");
        }

    }
    private void switchStateWithColor() throws IOException {
        if (lampController.getState()) {
            lampController.switchOff();
            stateField.setValue(getStateAsString());
            onOffButton.setText("On");
            icon.setColor("black");

        } else {
            lampController.switchOn(awtColor);
            stateField.setValue(getStateAsString());
            onOffButton.setText("Off");
            icon.setColor("orange");
        }

    }




}
