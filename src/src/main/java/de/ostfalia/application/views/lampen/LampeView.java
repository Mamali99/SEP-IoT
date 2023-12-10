package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.commandImp.*;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.tatu.ColorPicker;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route("/SE/LightAdapter")
public class LampeView extends BasicLayout {
    @Autowired
    private Java2NodeRedLampAdapter lampAdapter;
    private final RemoteController remoteController;
    private ColorPicker colorPicker;
    private ListBox<String> commandListBox;

    private List<Button> possibleButtons = new ArrayList<>();
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private VerticalLayout rightLayout = new VerticalLayout();
    private VerticalLayout initialButtonLayout = new VerticalLayout();
    private VerticalLayout newButtonLayout = new VerticalLayout();
    private Dialog buttonDialog = new Dialog();
    private Dialog undoDialog;
    private List<Button> customCommandButtons = new ArrayList<>();

    public LampeView(RemoteController remoteController) throws IOException {
        this.remoteController = remoteController;
        setupLayout();
    }

    private void setupLayout() {

        // Initialisiere die Buttons und ColorPicker
        Button turnOnButton = createButton("Turn On", VaadinIcon.POWER_OFF);
        turnOnButton.addClickListener(e -> executeCommand(new TurnOnCommand(lampAdapter)));

        Button turnOffButton = createButton("Turn Off", VaadinIcon.CLOSE);
        turnOffButton.addClickListener(e -> executeCommand(new TurnOffCommand(lampAdapter)));

        Button blinkButton = createButton("Blink", VaadinIcon.LIGHTBULB);
        blinkButton.addClickListener(e -> executeCommand(new BlinkCommand(lampAdapter, 2, 5000)));

        Button delayedCommandButton = createButton("Delayed Mode", VaadinIcon.HOURGLASS);
        delayedCommandButton.addClickListener(e -> executeDelayedCommands());

        Button partyModeButton = createButton("Party Mode", VaadinIcon.MUSIC);
        partyModeButton.addClickListener(e -> activatePartyMode());

        Button undoButton = createButton("Undo", VaadinIcon.ADJUST);
        undoButton.addClickListener(e -> openUndoDialog());

        // Füge die initialen Buttons zum Layout hinzu
        initialButtonLayout.add(turnOnButton, turnOffButton, blinkButton, delayedCommandButton, partyModeButton, undoButton);

        // Initialisiere die zusätzlichen Buttons
        possibleButtons.add(createButton("Button 1", VaadinIcon.ABACUS));
        possibleButtons.add(createButton("Button 2", VaadinIcon.ACADEMY_CAP));

        // Erstelle den "Plus"-Button
        Button plusButton = new Button("Plus", e -> openButtonDialog());
        plusButton.setIcon(VaadinIcon.PLUS.create());
        newButtonLayout.add(plusButton);

        colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbe wählen");
        colorPicker.addValueChangeListener(e -> executeCommand(new SetColorCommand(lampAdapter, hex2Rgb(e.getValue()))));

        // Dropdown-Menü für die Befehlshistorie
        commandListBox = new ListBox<>();
        undoDialog = new Dialog(commandListBox);
        updateCommandHistoryDropdown();

        // Layout

        buttonLayout.getStyle().set("background-color", "rgba(128, 128, 128, 0.2)"); // Transparent Grau
        buttonLayout.getStyle().set("border-radius", "20px"); // Abgerundete Kanten
        buttonLayout.getStyle().set("padding", "30px");

        rightLayout.getStyle().set("background-color", "rgba(128, 128, 128, 0.2)"); // Transparent Grau
        rightLayout.getStyle().set("border-radius", "20px"); // Abgerundete Kanten
        rightLayout.getStyle().set("padding", "30px");


        initialButtonLayout.getStyle().set("margin", "2px");
        initialButtonLayout.getStyle().set("padding", "2px");
        newButtonLayout.getStyle().set("padding", "2px");
        newButtonLayout.getStyle().set("margin", "2px");

        //virtuelle lampe
        VerticalLayout virtualLamp = new VerticalLayout(createLamp());

        buttonLayout.add(initialButtonLayout, newButtonLayout);
        rightLayout.add(virtualLamp, colorPicker, setupCustomCommandCreation());

        HorizontalLayout mainLayout = new HorizontalLayout(buttonLayout, rightLayout);
        this.setContent(mainLayout);
    }

    private void openUndoDialog() {
        List<Command> historyCommands = remoteController.getLastFiveCommands();

        if (historyCommands.isEmpty()) {
            Notification.show("Keine Befehle verfügbar");
            return;
        }

        ListBox<Command> commandListBox = new ListBox<>();
        commandListBox.setItems(historyCommands);
        commandListBox.setItemLabelGenerator(Command::toString);

        Dialog undoDialog = new Dialog(commandListBox);
        undoDialog.open();

        commandListBox.addValueChangeListener(event -> {
            Command selectedCommand = event.getValue();
            if (selectedCommand != null) {
                executeCommand(selectedCommand);
            }
        });
    }


    // Select Buttons to Add Option
    private void openButtonDialog() {
        buttonDialog.removeAll();

        for (Button button : possibleButtons) {
            button.addClickListener(e -> {
                addButton(button);
                possibleButtons.remove(button);
                buttonDialog.close();
            });
            buttonDialog.add(button);
        }

        buttonDialog.open();
    }


    private Component setupCustomCommandCreation() {
        List<String> availableCommands = Arrays.asList("Turn On", "Turn Off", "Blink");

        MultiSelectListBox<String> commandSelect = new MultiSelectListBox<>();
        commandSelect.setItems(availableCommands);

        Button submitButton = new Button("Create Custom Command");
        StringBuilder buttonName = new StringBuilder();

        submitButton.addClickListener(event -> {
            CustomCommand customCommand = new CustomCommand();

            for (String selectedCommand : commandSelect.getSelectedItems()) {
                switch (selectedCommand) {
                    case "Turn On":
                        customCommand.addCommand(new TurnOnCommand(lampAdapter));
                        break;
                    case "Turn Off":
                        customCommand.addCommand(new TurnOffCommand(lampAdapter));
                        break;
                    case "Blink":
                        customCommand.addCommand(new BlinkCommand(lampAdapter, 2, 5000)); // replace with actual params
                        break;
                    // Add remaining cases for the rest of your commands
                }
                buttonName.append(selectedCommand).append(" and ");
            }
            if (!buttonName.isEmpty()) {
                buttonName.setLength(buttonName.length() - 5);
            }
            Button newCustomCommandButton = createButton(buttonName.toString(), VaadinIcon.ABACUS); // You can change the icon as needed
            newCustomCommandButton.addClickListener(e -> executeCommand(customCommand));
            customCommandButtons.add(newCustomCommandButton);
            addButton(newCustomCommandButton);

            commandSelect.clear();
        });

        // Erstelle ein Layout und füge die Komponenten hinzu
        VerticalLayout layout = new VerticalLayout();
        layout.add(commandSelect, submitButton);
        return layout;
    }

    private Component createLamp() {
        Icon lampIcon = new Icon(VaadinIcon.LIGHTBULB);
        lampIcon.setSize("80px"); // Sie können die Größe an Ihre Bedürfnisse anpassen

        // Erstellen Sie die Box
        Div box = new Div(lampIcon);
        box.getStyle()
                .set("background-color", "lightgray") // Hintergrundfarbe
                .set("border-radius", "25px") // Abgerundete Kanten
                .set("padding", "10px") // Innenabstand
                .set("width", "120px") // Breite
                .set("height", "120px") // Höhe
                .set("display", "flex") // Flexbox-Layout verwenden
                .set("justify-content", "center") // Zentrieren Sie den Inhalt horizontal
                .set("align-items", "center"); // Zentrieren Sie den Inhalt vertikal

        return box;
    }


    private Button createButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.getStyle().set("width", "100px");
        button.getStyle().set("height", "100px");
        button.getStyle().set("position", "relative");
        button.getStyle().set("margin", "0px"); // Setzt den Außenabstand auf 0
        button.getStyle().set("padding", "0px"); // Setzt den Innenabstand auf 0
        button.getIcon().getStyle().set("position", "absolute");
        button.getIcon().getStyle().set("bottom", "10px");
        button.getIcon().getStyle().set("left", "50%");
        button.getIcon().getStyle().set("transform", "translateX(-50%)");
        return button;
    }

    private void addButton(Button button) {
        newButtonLayout.addComponentAtIndex(newButtonLayout.getComponentCount() - 1, button);
    }


    // vom Event Ausgelöst

    private void executeCommand(Command command) {
        try {
            remoteController.executeCommand(command);
            updateCommandHistoryDropdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCommandHistoryDropdown() {
        List<String> historyItems = remoteController.getLastFiveCommands()
                .stream()
                .map(Command::toString)
                .collect(Collectors.toList());
        commandListBox.setItems(historyItems);
    }

    private void undoSelectedCommand(String commandDescription) {
        int commandIndex = remoteController.getLastFiveCommands()
                .stream()
                .map(Command::toString)
                .collect(Collectors.toList())
                .indexOf(commandDescription);
        if (commandIndex != -1) {
            try {
                remoteController.undoCommand(commandIndex);
                updateCommandHistoryDropdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void activatePartyMode() {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        int[] intensities = {100, 200, 254};
        int blinkCount = 5;

        executeCommand(new PartyModeCommand(lampAdapter, blinkCount, colors, intensities));
    }

    private void executeDelayedCommands() {
        Command turnOnCommand = new TurnOnCommand(lampAdapter);
        Command turnOffCommand = new TurnOffCommand(lampAdapter);
        Command[] commands = {turnOnCommand, turnOffCommand};
        long delay = 5000;

        executeCommand(new DelayedCommands(lampAdapter, commands, delay));
    }

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }
}
