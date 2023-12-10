package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

@Route("/SE/LightAdapter")
public class LampeView extends BasicLayout {

    private final RemoteController remoteController;
    private ColorPicker colorPicker;
    private ComboBox<String> commandHistoryDropdown;

    public LampeView(RemoteController remoteController) throws IOException {
        this.remoteController = remoteController;
        setupLayout();
    }

    private void setupLayout() {
        // Initialisiere die Buttons und ColorPicker
        Button turnOnButton = createButton("Turn On", VaadinIcon.POWER_OFF);
        turnOnButton.addClickListener(e -> executeCommand(new TurnOnCommand(new Java2NodeRedLampAdapter())));

        Button turnOffButton = createButton("Turn Off", VaadinIcon.CLOSE);
        turnOffButton.addClickListener(e -> executeCommand(new TurnOffCommand(new Java2NodeRedLampAdapter())));

        Button blinkButton = createButton("Blink", VaadinIcon.LIGHTBULB);
        blinkButton.addClickListener(e -> executeCommand(new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000)));

        Button delayedCommandButton = createButton("Execute Delayed Commands", VaadinIcon.HOURGLASS);
        delayedCommandButton.addClickListener(e -> executeDelayedCommands());

        Button partyModeButton = createButton("Party Mode", VaadinIcon.MUSIC);
        partyModeButton.addClickListener(e -> activatePartyMode());

        colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbe wählen");
        colorPicker.addValueChangeListener(e -> executeCommand(new SetColorCommand(new Java2NodeRedLampAdapter(), hex2Rgb(e.getValue()))));

        // Dropdown-Menü für die Befehlshistorie
        commandHistoryDropdown = new ComboBox<>("Befehlshistorie", e -> undoSelectedCommand(e.getValue()));
        updateCommandHistoryDropdown();

        // Layout
        HorizontalLayout buttonLayout1 = new HorizontalLayout(turnOnButton, turnOffButton);
        HorizontalLayout buttonLayout2 = new HorizontalLayout(blinkButton, delayedCommandButton);
        HorizontalLayout buttonLayout3 = new HorizontalLayout(partyModeButton, colorPicker);

        VerticalLayout layout = new VerticalLayout(buttonLayout1, buttonLayout2, buttonLayout3, commandHistoryDropdown);
        this.setContent(layout);
    }

    // Buttons Generieren
    private Button createButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.getStyle().set("width", "100px");
        button.getStyle().set("height", "100px");
        button.getStyle().set("position", "relative");
        button.getIcon().getStyle().set("position", "absolute");
        button.getIcon().getStyle().set("bottom", "10px");
        button.getIcon().getStyle().set("left", "50%");
        button.getIcon().getStyle().set("transform", "translateX(-50%)");
        return button;
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
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        int[] intensities = {100, 200, 254};
        int blinkCount = 5;

        executeCommand(new PartyModeCommand(new Java2NodeRedLampAdapter(), blinkCount, colors, intensities));
    }

    private void executeDelayedCommands() {
        Command turnOnCommand = new TurnOnCommand(new Java2NodeRedLampAdapter());
        Command turnOffCommand = new TurnOffCommand(new Java2NodeRedLampAdapter());
        Command[] commands = {turnOnCommand, turnOffCommand};
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
