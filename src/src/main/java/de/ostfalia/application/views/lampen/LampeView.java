package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.commandImp.*;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.model.LampObserver;
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
public class LampeView extends BasicLayout implements LampObserver {
    @Autowired
    private Java2NodeRedLampAdapter lampAdapter;
    private final RemoteController remoteController;
    private ListBox<String> commandListBox;

    private List<Button> possibleButtons = new ArrayList<>();
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private HorizontalLayout virtualLampLayout = new HorizontalLayout();
    private HorizontalLayout customCommandLayout = new HorizontalLayout();

    private VerticalLayout rightLayoutFirstRow = new VerticalLayout();
    private VerticalLayout initialButtonLayout = new VerticalLayout();
    private VerticalLayout newButtonLayout = new VerticalLayout();
    private Dialog buttonDialog = new Dialog();

    private List<Button> customCommandButtons = new ArrayList<>();

    public LampeView(RemoteController remoteController, Java2NodeRedLampAdapter lampAdapter) throws IOException {
        this.remoteController = remoteController;
        this.lampAdapter = lampAdapter;
        this.lampAdapter.addObserver(this); // Registrieren als Observer
        setupLayout();
    }

    private void setupLayout() throws IOException {

        // Initialisiere die Buttons und ColorPicker
        Button turnOnButton = createButton("Turn On", VaadinIcon.POWER_OFF);
        turnOnButton.getElement().getStyle().set("background-color", "rgba(0, 255, 0, 0.2)"); // Grün und transparent
        turnOnButton.addClickListener(e -> executeCommand(new TurnOnCommand(lampAdapter)));
        turnOnButton.addClassName("button");
        turnOnButton.getIcon().addClassName("icon");

        Button turnOffButton = createButton("Turn Off", VaadinIcon.CLOSE);
        turnOffButton.getElement().getStyle().set("background-color", "rgba(255, 0, 0, 0.2)"); // Rot und transparent
        turnOffButton.addClickListener(e -> executeCommand(new TurnOffCommand(lampAdapter)));
        turnOffButton.addClassName("button");

        Button blinkButton = createButton("Blink", VaadinIcon.LIGHTBULB);
        blinkButton.addClickListener(e -> executeCommand(new BlinkCommand(lampAdapter, 2, 5000)));
        blinkButton.addClassName("button");

        Button partyModeButton = createButton("Party Mode", VaadinIcon.MUSIC);
        partyModeButton.addClickListener(e -> activatePartyMode());
        partyModeButton.addClassName("button");

        Button undoButton = createButton("Undo", VaadinIcon.ADJUST);
        undoButton.getElement().getStyle().set("background-color", "rgba(255, 255, 0, 0.2)"); // Gelb und transparent
        undoButton.addClickListener(e -> openUndoDialog());
        undoButton.addClassName("button");

        Button delayedCommandButton = createButton("Delayed", VaadinIcon.HOURGLASS);
        delayedCommandButton.addClickListener(e -> executeDelayedCommands());
        delayedCommandButton.addClassName("button");

        Button setColor = createButton("Set Color", VaadinIcon.ACADEMY_CAP);
        setColor.addClickListener(e -> {
            Dialog colorDialog = new Dialog();
            VerticalLayout layout = new VerticalLayout();
            setColor.addClassName("button");

            // Erstellen Sie einen ColorPicker zur Auswahl der Farbe
            ColorPicker colorPicker = new ColorPicker();
            try {
                colorPicker.setValue(colorToCss(lampAdapter.getColor()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            layout.add(colorPicker);

            // Erstellen Sie eine Schaltfläche zum Übernehmen der ausgewählten Farbe
            Button applyButton = new Button("Apply", click -> {
                executeCommand(new SetColorCommand(lampAdapter, hex2Rgb(colorPicker.getValue())));
                colorDialog.close();
            });
            applyButton.addClassName("button");
            layout.add(applyButton);

            colorDialog.add(layout);
            colorDialog.open();
        });

        this.addClassName("dark");
        // Füge die initialen Buttons zum Layout hinzu
        initialButtonLayout.add(turnOnButton, turnOffButton, blinkButton, delayedCommandButton, partyModeButton, undoButton);

        // Initialisiere die zusätzlichen Buttons
        Button setIntensity = createButton("Set Intensity", VaadinIcon.ABACUS);
        setIntensity.addClassName("button");
        setIntensity.addClickListener(e -> {
            Dialog intensityDialog = new Dialog();
            VerticalLayout layout = new VerticalLayout();
            IntegerField intensitySlider = new IntegerField();
            intensitySlider.setLabel("Lamp Intensity");
            intensitySlider.setHelperText("Max Intensity 254");
            try {
                intensitySlider.setValue((int) lampAdapter.getIntensity());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            intensitySlider.setMin(0);
            intensitySlider.setMax(254);
            layout.add(intensitySlider);

            // Erstellen Sie eine Schaltfläche zum Übernehmen der ausgewählten Intensität
            Button applyButton = new Button("Apply", click -> {
                executeCommand(new SetIntensityCommand(lampAdapter, intensitySlider.getValue()));
                intensityDialog.close();
            });
            applyButton.addClassName("button");
            layout.add(applyButton);

            intensityDialog.add(layout);
            intensityDialog.open();
        });
        possibleButtons.add(setIntensity);
        possibleButtons.add(setColor);
        setColor.addClassName("button");


        // Erstelle den "Plus"-Button
        Button plusButton = new Button("Plus", e -> openButtonDialog());
        plusButton.setIcon(VaadinIcon.PLUS.create());
        plusButton.addClassName("button");
        newButtonLayout.add(plusButton);


        // Dropdown-Menü für die Befehlshistorie
        commandListBox = new ListBox<>();
        commandListBox.addValueChangeListener(e -> undoSelectedCommand(e.getValue()));
        updateCommandHistoryDropdown();

        // Layout

        buttonLayout.addClassName("common-style");
        customCommandLayout.addClassName("common-style");
        virtualLampLayout.addClassName("common-style");

        initialButtonLayout.getStyle().set("margin", "2px");
        initialButtonLayout.getStyle().set("padding", "2px");
        newButtonLayout.getStyle().set("padding", "2px");
        newButtonLayout.getStyle().set("margin", "2px");

        //virtuelle lampe
        VerticalLayout virtualLamp = new VerticalLayout(createLamp());

        buttonLayout.add(initialButtonLayout, newButtonLayout);
        rightLayoutFirstRow.add(virtualLamp);
        virtualLampLayout.add(rightLayoutFirstRow);
        customCommandLayout.add(setupCustomCommandCreation());

        HorizontalLayout mainLayout = new HorizontalLayout(buttonLayout, virtualLampLayout, customCommandLayout);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Horizontales Zentrieren
        this.setContent(mainLayout);
    }

    private Div undoDialog;


    private Div createUndoDialog() {
        // Create the title, list box, and close button
        H4 UndoTitel = new H4("Click to Undo");
        UndoTitel.getStyle().set("text-align", "center"); // Center the title

        Button closeButton = new Button("Close");
        closeButton.getStyle().set("margin-top", "10px");
        closeButton.addClassName("button");

        Div dialogDiv = new Div();
        dialogDiv.add(UndoTitel, commandListBox, closeButton);

        // Style the Div
        dialogDiv.getStyle().set("width", "250px"); // Set the width
        dialogDiv.addClassName("common-style");
        closeButton.addClickListener(event -> dialogDiv.setVisible(false));

        return dialogDiv;
    }

    private void openUndoDialog() {
        // Remove the old undo dialog from the layout
        if (undoDialog != null) {
            rightLayoutFirstRow.remove(undoDialog);
        }

        // Create a new undo dialog and add it to the layout
        undoDialog = createUndoDialog();
        rightLayoutFirstRow.add(undoDialog);
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

    Div lampBox;
    Span statusLabel;
    Span intensityLvl;
    Icon lampIcon;

    private Component createLamp() throws IOException {
        lampIcon = new Icon(VaadinIcon.LIGHTBULB);
        lampIcon.setSize("100px");
        lampIcon.setColor("black");

        intensityLvl = new Span("Intensity: " + lampAdapter.getIntensity());
        intensityLvl.getStyle().set("font-size", "larger");


        statusLabel = new Span();
        statusLabel.getStyle().set("font-size", "larger");


        updateStatusLabel();

        lampBox = new Div();
        lampBox.getStyle()
                .set("background-color", "rgba(" + lampAdapter.getColor().getRed() + ", " + lampAdapter.getColor().getGreen() + ", " + lampAdapter.getColor().getBlue() + ", 0.5)")
                .set("border-radius", "25px")
                .set("padding", "10px")
                .set("width", "250px")
                .set("height", "250px")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("box-shadow", "0px 0px 10px rgba(255, 255, 255, 0.1)")
                .set("position", "relative");
        lampBox.add(lampIcon);

        VerticalLayout lampLayout = new VerticalLayout();
        lampLayout.add(lampBox, statusLabel, intensityLvl);
        lampLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        return lampLayout;
    }

    private void updateStatusLabel() throws IOException {
        if (lampAdapter.getState()) {
            statusLabel.setText("The lamp is: ON");
            lampIcon.setColor("white");

        } else {
            statusLabel.setText("The lamp is: OFF");
            lampIcon.setColor("black");
        }
        intensityLvl.setText("Intensity: " + lampAdapter.getIntensity());
    }

    private void updateLampColor(Color color) {
        // Update the Div background color
        lampBox.getStyle().set("background-color", "rgba(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.5)");
    }

    private void updateGUI() throws IOException {
        updateCommandHistoryDropdown();
        updateLampColor(lampAdapter.getColor());
        updateStatusLabel();
    }

    private Button createButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.getStyle().set("width", "100px");
        button.getStyle().set("height", "100px");
        button.getStyle().set("position", "relative");
        button.getStyle().set("margin", "2px");
        button.getStyle().set("padding", "0px");
        button.getIcon().getStyle().set("position", "absolute");
        button.getIcon().getStyle().set("bottom", "10px");
        button.getIcon().getStyle().set("left", "50%");
        button.getIcon().getStyle().set("transform", "translateX(-50%)");
        button.addClassName("button");
        return button;
    }

    private void addButton(Button button) {
        newButtonLayout.addComponentAtIndex(newButtonLayout.getComponentCount() - 1, button);
    }


    // vom Event Ausgelöst

    private void executeCommand(Command command) {
        try {
            remoteController.executeCommand(command);
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
                Notification.show("Undo operation performed for: " + commandDescription);
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

    public String colorToCss(Color color) {
        // Convert the Color to a CSS color string
        return String.format("#%02x%02x%02x",
                color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void updateLampState() throws IOException {
        updateGUI();
    }
}
