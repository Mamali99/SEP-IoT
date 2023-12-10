package de.ostfalia.application.views.lampen;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.lamp.commandImp.*;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.views.BasicLayout;
import org.vaadin.addons.tatu.ColorPicker;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route("/SE/LampControl")
public class LampRemoteControlView extends BasicLayout {

    private RemoteController remoteController;
    private ColorPicker colorPicker;
    private ComboBox<String> commandHistoryDropdown;

    List<Button> customCommandButtons = new ArrayList<>();

    VerticalLayout layout = new VerticalLayout();

    private Div lampRepresentation;

    public LampRemoteControlView(RemoteController remoteController) throws IOException {
        this.remoteController = remoteController;

        setupLayout();
    }

    private void setupLayout() {
        this.lampRepresentation = new Div();
        this.lampRepresentation.getStyle().set("width", "50px").set("height", "50px");

        // Initialisiere die Buttons und ColorPicker
        Button turnOnButton = new Button("Turn On", e -> executeCommand(new TurnOnCommand(new Java2NodeRedLampAdapter())));
        Button turnOffButton = new Button("Turn Off", e -> executeCommand(new TurnOffCommand(new Java2NodeRedLampAdapter())));
        Button blinkButton = new Button("Blink", e -> executeCommand(new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000)));
        Button delayedCommandButton = new Button("Execute Delayed Commands", e -> executeDelayedCommands());
        Button partyModeButton = new Button("Party Mode", e -> activatePartyMode());
        Button customButton = new Button("Turn On and Blink", e -> createAndExecuteCustomCommand());

        colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbe wählen");
        colorPicker.addValueChangeListener(e -> updateLampRepresentationColor(hex2Rgb(e.getValue())));
        colorPicker.addValueChangeListener(e -> executeCommand(new SetColorCommand(new Java2NodeRedLampAdapter(), hex2Rgb(e.getValue()))));

        // Dropdown-Menü für die Befehlshistorie
        commandHistoryDropdown = new ComboBox<>("Befehlshistorie", e -> undoSelectedCommand(e.getValue()));
        updateCommandHistoryDropdown();

        // Layout
        layout = new VerticalLayout(turnOnButton, customButton, turnOffButton,
                blinkButton, delayedCommandButton, partyModeButton, colorPicker, commandHistoryDropdown,
                lampRepresentation);
        List<Component> customCommandComponents = setupCustomCommandCreation();
        for (Button customCommandButton : customCommandButtons) {
            layout.add(customCommandButton);
        }
        customCommandComponents.stream().forEach(layout::add);

        this.setContent(layout);
    }

    private void executeCommand(Command command) {
        try {
            remoteController.executeCommand(command);
            updateCommandHistoryDropdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void updateLampRepresentationColor(Color color) {
        String rgbColor = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
        this.lampRepresentation.getStyle().set("background", rgbColor);

        // Execute SetColorCommand
        SetColorCommand setColorCommand = new SetColorCommand(new Java2NodeRedLampAdapter(), color);
        executeCommand(setColorCommand);
    }

    private List<Component> setupCustomCommandCreation() {
        List<String> availableCommands = Arrays.asList("Turn On", "Turn Off", "Blink" );

        MultiSelectListBox<String> commandSelect = new MultiSelectListBox<>();
        commandSelect.setItems(availableCommands);

        Button submitButton = new Button("Create Custom Command");

        submitButton.addClickListener(event -> {
            CustomCommand customCommand = new CustomCommand();

            for (String selectedCommand : commandSelect.getSelectedItems()) {
                switch (selectedCommand) {
                    case "Turn On":
                        customCommand.addCommand(new TurnOnCommand(new Java2NodeRedLampAdapter()));
                        break;
                    case "Turn Off":
                        customCommand.addCommand(new TurnOffCommand(new Java2NodeRedLampAdapter()));
                        break;
                    case "Blink":
                        customCommand.addCommand(new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000)); // replace with actual params
                        break;
                    // Add remaining cases for the rest of your commands
                }
            }
            Button newCustomCommandButton = new Button("Custom Command " + (customCommandButtons.size() + 1), e -> executeCommand(customCommand));
            customCommandButtons.add(newCustomCommandButton);
            layout.add(newCustomCommandButton);

            executeCommand(customCommand);
            commandSelect.clear();
        });

        return Arrays.asList(commandSelect, submitButton);
    }

    private void updateCommandHistoryDropdown() {
        List<String> historyItems = remoteController.getLastFiveCommands()
                .stream()
                .map(Command::toString)
                .collect(Collectors.toList());
        commandHistoryDropdown.setItems(historyItems);
    }

    private void createAndExecuteCustomCommand() {
        CustomCommand customCommand = new CustomCommand();
        TurnOnCommand turnOnCommand = new TurnOnCommand(new Java2NodeRedLampAdapter());
        BlinkCommand blinkCommand = new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000);
        customCommand.addCommand(turnOnCommand);
        customCommand.addCommand(blinkCommand);

        executeCommand(customCommand);
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
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE };
        int[] intensities = { 100, 200, 254 };
        int blinkCount = 5;

        executeCommand(new PartyModeCommand(new Java2NodeRedLampAdapter(), blinkCount, colors, intensities));
    }

    private void executeDelayedCommands() {
        Command turnOnCommand = new TurnOnCommand(new Java2NodeRedLampAdapter());
        Command turnOffCommand = new TurnOffCommand(new Java2NodeRedLampAdapter());
        Command[] commands = { turnOnCommand, turnOffCommand };
        long delay = 5000;

        executeCommand(new DelayedCommands(new Java2NodeRedLampAdapter(), commands, delay));
    }

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }
}
