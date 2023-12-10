package de.ostfalia.application.views.lampen;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.List;
import java.util.stream.Collectors;

@Route("/SE/LampControl")
public class LampRemoteControlView extends BasicLayout {

    private RemoteController remoteController;
    private ColorPicker colorPicker;
    private ComboBox<String> commandHistoryDropdown;

    public LampRemoteControlView(RemoteController remoteController) throws IOException {
        this.remoteController = remoteController;

        setupLayout();
    }

    private void setupLayout() {
        // Initialisiere die Buttons und ColorPicker
        Button turnOnButton = new Button("Turn On", e -> executeCommand(new TurnOnCommand(new Java2NodeRedLampAdapter())));
        Button turnOffButton = new Button("Turn Off", e -> executeCommand(new TurnOffCommand(new Java2NodeRedLampAdapter())));
        Button blinkButton = new Button("Blink", e -> executeCommand(new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000)));
        Button delayedCommandButton = new Button("Execute Delayed Commands", e -> executeDelayedCommands());
        Button partyModeButton = new Button("Party Mode", e -> activatePartyMode());
        colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbe wählen");
        colorPicker.addValueChangeListener(e -> executeCommand(new SetColorCommand(new Java2NodeRedLampAdapter(), hex2Rgb(e.getValue()))));

        // Dropdown-Menü für die Befehlshistorie
        commandHistoryDropdown = new ComboBox<>("Befehlshistorie", e -> undoSelectedCommand(e.getValue()));
        updateCommandHistoryDropdown();


        // Layout
        VerticalLayout layout = new VerticalLayout(turnOnButton, turnOffButton, blinkButton, delayedCommandButton, partyModeButton, colorPicker, commandHistoryDropdown);
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

    private void updateCommandHistoryDropdown() {
        List<String> historyItems = remoteController.getLastFiveCommands()
                .stream()
                .map(Command::toString)
                .collect(Collectors.toList());
        commandHistoryDropdown.setItems(historyItems);
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
