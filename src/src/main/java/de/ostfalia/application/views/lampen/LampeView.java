package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.commandImp.*;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.model.LampObserver;
import de.ostfalia.application.data.lamp.service.BikeLampScheduler;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;
import de.ostfalia.application.views.BasicLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.tatu.ColorPicker;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Route("/SE/LightAdapter")
public class LampeView extends BasicLayout implements LampObserver {
    @Autowired
    private Java2NodeRedLampAdapter lampAdapter;
    @Autowired
    private BikeLampScheduler bikeLampScheduler;
    private final RemoteController remoteController;
    private BikeService bikeService;


    private ListBox<String> commandListBox;

    private List<Button> possibleButtons = new ArrayList<>();
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private HorizontalLayout virtualLampLayout = new HorizontalLayout();
    private VerticalLayout customCommandLayout = new VerticalLayout();

    private VerticalLayout rightLayoutFirstRow = new VerticalLayout();
    private VerticalLayout initialButtonLayout = new VerticalLayout();
    private VerticalLayout newButtonLayout = new VerticalLayout();
    private Dialog buttonDialog = new Dialog();

    private List<Button> customCommandButtons = new ArrayList<>();

    // Aktuelles BlinkCommand speichern
    private BlinkCommand currentBlinkCommand;

    private UI ui;
    private PartyModeCommand currentPartyModeCommand;
    private VirtualLampComponent virtualLampComponent;



    public LampeView(BikeService bikeService, RemoteController remoteController, Java2NodeRedLampAdapter lampAdapter, BikeLampScheduler bikeLampScheduler) throws IOException {
        this.remoteController = remoteController;
        this.lampAdapter = lampAdapter;
        this.lampAdapter.addObserver(this); // Registrieren als Observer
        this.bikeService = bikeService;
        this.bikeLampScheduler = bikeLampScheduler;
        setupLayout();
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        ui = attachEvent.getUI();
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
        blinkButton.addClickListener(e -> executeCommand(new BlinkCommand(lampAdapter, 3, 2000)));
        blinkButton.addClassName("button");

        Button partyModeButton = createButton("Party Mode", VaadinIcon.MUSIC);
        partyModeButton.addClickListener(e -> activatePartyMode());
        partyModeButton.addClassName("button");

        Button raceButton = createButton("Race", VaadinIcon.MUSIC);
        raceButton.addClickListener(e -> createRaceDialog());
        raceButton.addClassName("button");


        Button undoButton = createButton("Undo", VaadinIcon.ADJUST);
        undoButton.getElement().getStyle().set("background-color", "rgba(255, 255, 0, 0.2)"); // Gelb und transparent
        undoButton.addClickListener(e -> openUndoDialog());
        undoButton.addClassName("button");



        Button setColor = createButton("Set Color", VaadinIcon.ACADEMY_CAP);
        setColor.addClickListener(e -> {
            try {
                openColorPickerDialog(color -> executeCommand(new SetColorCommand(lampAdapter, color)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        //Small spedup for channel select when not in the button, fetch is done while initialising the component
        List<Integer> availableChannels = bikeService.getAvailableChannels();

        Button setDrive = createButton("Drive", VaadinIcon.ABACUS);
        setDrive.addClassName("button");
        setDrive.addClickListener(e -> {
            Dialog driveDialog = new Dialog();
            VerticalLayout layout = new VerticalLayout();
            ComboBox<Integer> channelSelect = new ComboBox<>("Select bike channel");
            channelSelect.setLabel("Choose a Bike");


            channelSelect.setItems(availableChannels);
            layout.add(channelSelect);

            // Erstellen Sie eine Schaltfläche zum Übernehmen der ausgewählten Intensität
            Button applyButton = new Button("Run", click -> {
                Integer selectedChannel = channelSelect.getValue();
                bikeLampScheduler.setSelectedChannel(selectedChannel);
                bikeLampScheduler.enableDriveCommand();
                driveDialog.close();
            });
            applyButton.addClassName("button");
            layout.add(applyButton);

            driveDialog.add(layout);
            driveDialog.open();
        });
        possibleButtons.add(setDrive);
        possibleButtons.add(setColor);
        setColor.addClassName("button");

        this.addClassName("dark");
        // Füge die initialen Buttons zum Layout hinzu
        initialButtonLayout.add(turnOnButton, turnOffButton, raceButton, setDrive, blinkButton, partyModeButton, undoButton);

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
        virtualLampComponent = new VirtualLampComponent();
        rightLayoutFirstRow.add(virtualLampComponent);

        buttonLayout.add(initialButtonLayout, newButtonLayout);
        virtualLampLayout.add(rightLayoutFirstRow);
        customCommandLayout.add(setupCustomCommandCreation());



        HorizontalLayout mainLayout = new HorizontalLayout(buttonLayout, virtualLampLayout, customCommandLayout);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Horizontales Zentrieren
        this.setContent(mainLayout);
    }

    private void createRaceDialog() {
        Dialog raceDialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        ComboBox<Integer> bikeChannelSelect1 = new ComboBox<>("Select Bike 1 Channel");
        ComboBox<Integer> bikeChannelSelect2 = new ComboBox<>("Select Bike 2 Channel");
        List<Integer> availableChannels = bikeService.getAvailableChannels();
        bikeChannelSelect1.setItems(availableChannels);
        bikeChannelSelect2.setItems(availableChannels);

        Button applyButton = new Button("Start Race", click -> {
            Integer selectedChannel1 = bikeChannelSelect1.getValue();
            Integer selectedChannel2 = bikeChannelSelect2.getValue();
            bikeLampScheduler.setBikeChannels(selectedChannel1, selectedChannel2);
            bikeLampScheduler.enableRaceCommand();
            raceDialog.close();
        });
        applyButton.addClassName("button");

        layout.add(bikeChannelSelect1, bikeChannelSelect2, applyButton);
        raceDialog.add(layout);
        raceDialog.open();
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
        List<String> availableCommands = Arrays.asList("Turn On", "Turn Off", "Set Intensity", "Set Color");
        MultiSelectListBox<String> commandSelect = new MultiSelectListBox<>();
        commandSelect.setItems(availableCommands);
        StringBuilder buttonName = new StringBuilder();
        CustomCommand newCommand = new CustomCommand();

        // Create input fields
        IntegerField intensityField = new IntegerField("Intensity");
        intensityField.setMin(0);
        intensityField.setMax(100);
        intensityField.setVisible(false);

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setVisible(false);

        // Create container for inputs
        Div inputContainer = new Div(intensityField, colorPicker);
        inputContainer.addClassName("common-style");

        commandSelect.addValueChangeListener(event -> {
            intensityField.setVisible(event.getValue().contains("Set Intensity"));
            colorPicker.setVisible(event.getValue().contains("Set Color"));
        });

        Button submitButton = new Button("Create Custom Command");
        submitButton.addClassName("button");
        submitButton.addClickListener(event -> {
            buttonName.setLength(0); // Clear previous command names

            for (String selectedCommand : commandSelect.getSelectedItems()) {
                switch (selectedCommand) {
                    case "Turn On":
                        newCommand.addCommand(new TurnOnCommand(lampAdapter));
                        buttonName.append(selectedCommand);
                        break;
                    case "Turn Off":
                        newCommand.addCommand(new TurnOffCommand(lampAdapter));
                        buttonName.append(selectedCommand);
                        break;

                    case "Set Intensity":
                        newCommand.addCommand(new SetIntensityCommand(lampAdapter, intensityField.getValue()));
                        buttonName.append("Intensity:").append(intensityField.getValue()).append(" ");
                        break;
                    case "Set Color":
                        Color selectedColor = hex2Rgb(colorPicker.getValue());
                        newCommand.addCommand(new SetColorCommand(lampAdapter, selectedColor));
                        buttonName.append("Color:").append(colorPicker.getValue()).append(" ");
                        break;
                    // Add remaining cases for the rest of your commands
                }
            }

            if (!buttonName.toString().isEmpty()) {
                Button newCustomCommandButton = createButton(buttonName.toString(), VaadinIcon.ABACUS);
                newCustomCommandButton.addClickListener(e -> {
                    try {
                        newCommand.execute();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                customCommandButtons.add(newCustomCommandButton);
                newButtonLayout.add(newCustomCommandButton);
            }

            commandSelect.clear();
        });

        return new VerticalLayout(commandSelect, inputContainer, submitButton);
    }


    private void openIntensityDialog(Consumer<Integer> intensityConsumer) {
        Dialog intensityDialog = new Dialog();
        IntegerField intensityField = new IntegerField("Intensity");
        intensityField.setMin(0);
        intensityField.setMax(100); // Set appropriate max value

        Button applyButton = new Button("Apply", e -> {
            intensityConsumer.accept(intensityField.getValue());
            intensityDialog.close();
        });

        intensityDialog.add(new VerticalLayout(intensityField, applyButton));
        intensityDialog.open();
    }

    private void openColorPickerDialog(Consumer<Color> colorConsumer) throws IOException {
        Dialog colorDialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(colorToCss(lampAdapter.getColor()));
        layout.add(colorPicker);

        Button applyButton = new Button("Apply", click -> {
            Color selectedColor = hex2Rgb(colorPicker.getValue());
            colorConsumer.accept(selectedColor);
            colorDialog.close();
        });
        applyButton.addClassName("button");
        layout.add(applyButton);

        colorDialog.add(layout);
        colorDialog.open();
    }


    private void updateGUI() throws IOException {
        updateCommandHistoryDropdown();
    }

    private Button createButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.setIconAfterText(true);

        // Set width and dynamically adjust height
        button.getStyle().set("width", "180px");
        button.getStyle().set("height", "80px"); // Adjust height dynamically

        // Positioning and padding
        button.getStyle().set("position", "relative");
        button.getStyle().set("margin", "2px");
        button.getStyle().set("padding", "5px");

        // Smaller font size
        button.getStyle().set("font-size", "0.9em");

        // Icon positioning
        button.getIcon().getStyle().set("position", "absolute");
        button.getIcon().getStyle().set("bottom", "5px");
        button.getIcon().getStyle().set("left", "50%");
        button.getIcon().getStyle().set("transform", "translateX(-50%)");
        button.getIcon().getStyle().set("padding", "5px");
        button.addClassName("button");

        // Enable multiline text
        button.getElement().getStyle().set("white-space", "normal");

        return button;
    }


    private void addButton(Button button) {
        newButtonLayout.addComponentAtIndex(newButtonLayout.getComponentCount() - 1, button);
    }


    private void executeCommand(Command command) {
        try {
            if (command instanceof BlinkCommand) {
                if (currentBlinkCommand != null) {
                    // Stoppt das aktuelle Blinken, bevor ein neues gestartet wird
                    currentBlinkCommand.stopBlinking();


                }
                if(currentPartyModeCommand != null){
                    currentPartyModeCommand.stopPartyMode();

                }
                // Führe das neue BlinkCommand aus und speichere es als das aktuelle
                remoteController.executeCommand(command);
                currentBlinkCommand = (BlinkCommand) command;
            } else if (command instanceof PartyModeCommand) {
                if (currentPartyModeCommand != null) {
                    // Stoppt den aktuellen Party-Modus, bevor ein neuer gestartet wird
                    currentPartyModeCommand.stopPartyMode();
                }
                if(currentBlinkCommand != null){
                    currentBlinkCommand.stopBlinking();
                }
                // Führe das neue PartyModeCommand aus und speichere es als das aktuelle
                remoteController.executeCommand(command);
                currentPartyModeCommand = (PartyModeCommand) command;
            } else {
                // Für alle anderen Befehle, stoppe laufende Befehle und führe den neuen aus
                if (currentBlinkCommand != null) {
                    currentBlinkCommand.stopBlinking();
                    currentBlinkCommand = null;
                }
                if (currentPartyModeCommand != null) {
                    currentPartyModeCommand.stopPartyMode();
                    currentPartyModeCommand = null;
                }
                remoteController.executeCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateCommandHistoryDropdown();
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
            // Stoppt das aktuelle BlinkCommand und PartyModeCommand, falls sie laufen
            if (currentBlinkCommand != null) {
                currentBlinkCommand.stopBlinking();
                currentBlinkCommand = null;
            }
            if (currentPartyModeCommand != null) {
                currentPartyModeCommand.stopPartyMode();
                currentPartyModeCommand = null;
            }
            try {
                remoteController.undoCommand(commandIndex);
                Notification.show("Undo operation performed for: " + commandDescription);
                bikeLampScheduler.disableDriveCommand();
                updateGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Button createStopDriveButton() {
        Button stopDriveButton = new Button("Stop Drive");
        stopDriveButton.addClickListener(e -> {
            bikeLampScheduler.disableDriveCommand();
        });
        return stopDriveButton;
    }


    private void activatePartyMode() {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        int[] intensities = {100, 200, 254};
        int blinkCount = 5;

        executeCommand(new PartyModeCommand(lampAdapter, blinkCount, colors, intensities));
    }
/*
    private void executeDelayedCommands() {
        Command turnOnCommand = new TurnOnCommand(lampAdapter);
        Command turnOffCommand = new TurnOffCommand(lampAdapter);
        Command[] commands = {turnOnCommand, turnOffCommand};
        long delay = 5000;

        executeCommand(new DelayedCommands(lampAdapter, commands, delay));
    }

 */

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
        ui.access(() -> {
            try {
                Color color = lampAdapter.getColor();
                float intensity = lampAdapter.getIntensity();
                boolean isOn = lampAdapter.getState();
                virtualLampComponent.updateLampState(isOn, color, (int) intensity);
                updateGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateCommandHistoryDropdown();
        });
    }
}
