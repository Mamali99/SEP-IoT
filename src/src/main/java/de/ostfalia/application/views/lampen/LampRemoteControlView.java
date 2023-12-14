package de.ostfalia.application.views.lampen;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.entity.BicycleID;
import de.ostfalia.application.data.lamp.commandImp.*;
import de.ostfalia.application.data.lamp.controller.RemoteController;
import de.ostfalia.application.data.lamp.model.Command;
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
import java.util.stream.Collectors;

import static com.vaadin.flow.dom.ElementFactory.createButton;

@Route("/SE/LampControl")
public class LampRemoteControlView extends BasicLayout {

    private RemoteController remoteController;
    @Autowired
    private BikeLampScheduler bikeLampScheduler;
    private ColorPicker colorPicker;
    private ComboBox<String> commandHistoryDropdown;
    private BikeDriveCommand bikeDriveCommand;

    @Autowired
    private BikeService bikeService;

    List<Button> customCommandButtons = new ArrayList<>();

    VerticalLayout layout = new VerticalLayout();

    private Div lampRepresentation;

    public LampRemoteControlView(RemoteController remoteController, BikeService bikeService) throws IOException {
        this.remoteController = remoteController;
        this.bikeService = bikeService;

        setupLayout();
    }

    private void setupLayout() {
        this.lampRepresentation = new Div();
        this.lampRepresentation.getStyle().set("width", "50px").set("height", "50px");

        // Initialisiere die Buttons und ColorPicker
        // Button für BikeDriveCommand hinzufügen
        Button bikeDriveButton = new Button("Bike Drive", e -> executeCommand(new BikeDriveCommand(new Java2NodeRedLampAdapter(), bikeService, 5)));
        Button turnOnButton = new Button("Turn On", e -> executeCommand(new TurnOnCommand(new Java2NodeRedLampAdapter())));
        Button turnOffButton = new Button("Turn Off", e -> executeCommand(new TurnOffCommand(new Java2NodeRedLampAdapter())));
        Button blinkButton = new Button("Blink", e -> executeCommand(new BlinkCommand(new Java2NodeRedLampAdapter(), 2, 5000)));
        Button delayedCommandButton = new Button("Execute Delayed Commands", e -> executeDelayedCommands());
        Button partyModeButton = new Button("Party Mode", e -> activatePartyMode());
        Button customButton = new Button("Turn On and Blink", e -> createAndExecuteCustomCommand());
        Button stopDriveButton = createStopDriveButton();



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
                lampRepresentation, bikeDriveButton);
        List<Component> customCommandComponents = setupCustomCommandCreation();
        for (Button customCommandButton : customCommandButtons) {
            layout.add(customCommandButton);
        }

        ComboBox<Integer> channelSelect = getChannelSelect();
        layout.add(channelSelect);
        layout.add(stopDriveButton);

        customCommandComponents.stream().forEach(layout::add);
        this.setContent(layout);
    }

    private ComboBox<Integer> getChannelSelect() {
        ComboBox<Integer> channelSelect = new ComboBox<>("Select bike channel");

        List<Integer> availableChannels = bikeService.getAvailableChannels();
        channelSelect.setItems(availableChannels);

        channelSelect.addValueChangeListener(e -> {
            int selectedChannel = e.getValue();
            bikeLampScheduler.setSelectedChannel(selectedChannel);
            bikeLampScheduler.enableDriveCommand();
        });
        return channelSelect;
    }

    private void executeCommand(Command command) {
        try {
            remoteController.executeCommand(command);
            updateCommandHistoryDropdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button createStopDriveButton() {
        Button stopDriveButton = new Button("Stopp Fahrt");
        stopDriveButton.addClickListener(e -> {
            bikeLampScheduler.disableDriveCommand(); // This method needs to be defined in your BikeLampScheduler
        });
        return stopDriveButton;
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
        StringBuilder buttonName = new StringBuilder();

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
                buttonName.append(selectedCommand).append(" and ");
            }
            if(!buttonName.isEmpty()){
                buttonName.setLength(buttonName.length() - 5);
            }
            Button newCustomCommandButton = new Button(buttonName.toString(),
                    e -> executeCommand(customCommand));
            customCommandButtons.add(newCustomCommandButton);
            layout.add(newCustomCommandButton);

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
