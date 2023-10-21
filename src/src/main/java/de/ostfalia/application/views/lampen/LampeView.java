package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.lampController.LampController;
import de.ostfalia.application.views.BasicLayout;

import java.io.IOException;

@Route("/SE/LightAdapter")

public class LampeView extends BasicLayout {

    private LampController lampController;
    private VerticalLayout pageLayout = new VerticalLayout();

    private H4 nameLabel;
    private TextField nameField;
    private TextField stateField;
    private Button onOffButton;
    private Icon icon;
    private Button nameButton;
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
        stateField = new TextField("Lampenzustand");
        stateField.setReadOnly(true);
        onOffButton = new Button("ON/OFF", e -> {
            try {
                switchState();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        nameField = new TextField("Namen Ändern");
        nameField.setReadOnly(true);
        nameButton = new Button("Ändern", e -> enableNameChange());
        nameField.addValueChangeListener(event -> {
            String text = event.getValue();
            try {
                lampController.setName(text);
                nameLabel.setText(lampController.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });


        h2.setText("Lampen Einstellungen");
        icon.getElement().setAttribute("icon", "vaadin:lightbulb");
        nameLabel.setText("Meine Lampe");
        pageLayout.add(h2);
        pageLayout.add(hr);
        layoutRow.add(icon);
        layoutRow.add(nameLabel);
        pageLayout.add(layoutRow);
        pageLayout.add(hr2);
        pageLayout.add(stateField);
        pageLayout.add(onOffButton);
        pageLayout.add(nameField);
        pageLayout.add(nameButton);

        this.setContent(pageLayout);
        this.getTitle().setText("Lampensteuerung");
    }
    // Funktionen
    private void switchState() throws IOException {
        if (lampController.getState()) {
            lampController.switchOff();
            stateField.setValue("Ausgeschaltet");
            onOffButton.setText("On");
            icon.setColor("black");

        } else {
            lampController.switchOn();
            stateField.setValue("Angeschaltet");
            onOffButton.setText("Off");
            icon.setColor("orange");
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

}
