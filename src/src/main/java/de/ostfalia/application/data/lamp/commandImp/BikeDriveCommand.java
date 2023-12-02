package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class BikeDriveCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private BikeService bikeService;
    private int bikeChannel;
    private static final BigDecimal MAX_SPEED = BigDecimal.valueOf(50); // Maximalgeschwindigkeit laut pdf

    public BikeDriveCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService, int bikeChannel) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel = bikeChannel;
    }

    @Override
    public void execute() throws IOException {

    }

    private BigDecimal calculateSpeed(List<Bicycle> bikeData) {
        return BigDecimal.ZERO;
    }
}
