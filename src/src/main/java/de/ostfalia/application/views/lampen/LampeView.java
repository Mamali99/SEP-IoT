package de.ostfalia.application.views.lampen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("/SE/LightAdapter")
public class LampeView extends BasicLayout {

    private final RemoteController remoteController;
    private ColorPicker colorPicker;
    private ComboBox<String> commandHistoryDropdown;

    private List<Button> possibleButtons = new ArrayList<>();
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private VerticalLayout rightLayout = new VerticalLayout();
    private VerticalLayout initialButtonLayout = new VerticalLayout();
    private VerticalLayout newButtonLayout = new VerticalLayout();
    private Dialog buttonDialog = new Dialog();

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

        // Füge die initialen Buttons zum Layout hinzu
        initialButtonLayout.add(turnOnButton, turnOffButton, blinkButton, delayedCommandButton, partyModeButton);

        // Initialisiere die zusätzlichen Buttons
        possibleButtons.add(createButton("Button 1", VaadinIcon.ABACUS));
        possibleButtons.add(createButton("Button 2", VaadinIcon.ACADEMY_CAP));

        // Erstelle den "Plus"-Button
        Button plusButton = new Button("Plus", e -> openButtonDialog());
        plusButton.setIcon(VaadinIcon.PLUS.create());
        newButtonLayout.add(plusButton);

        colorPicker = new ColorPicker();
        colorPicker.setLabel("Farbe wählen");
        colorPicker.addValueChangeListener(e -> executeCommand(new SetColorCommand(new Java2NodeRedLampAdapter(), hex2Rgb(e.getValue()))));

        // Dropdown-Menü für die Befehlshistorie
        commandHistoryDropdown = new ComboBox<>("Befehlshistorie", e -> undoSelectedCommand(e.getValue()));
        updateCommandHistoryDropdown();

        // Layout
        initialButtonLayout.getStyle().set("margin", "2px");
        newButtonLayout.getStyle().set("margin", "2px");
        initialButtonLayout.getStyle().set("padding", "0px");
        newButtonLayout.getStyle().set("padding", "0px");
        buttonLayout.add(initialButtonLayout, newButtonLayout);
        rightLayout.add(colorPicker, commandHistoryDropdown);
        HorizontalLayout mainLayout = new HorizontalLayout(buttonLayout, rightLayout);
        this.setContent(mainLayout);
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
