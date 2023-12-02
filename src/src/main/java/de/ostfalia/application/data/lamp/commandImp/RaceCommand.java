package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class RaceCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private BikeService bikeService;
    private int bikeChannel1;
    private int bikeChannel2;
    private Color colorBike1;
    private Color colorBike2;

    public RaceCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService,
                       int bikeChannel1, int bikeChannel2,
                       Color colorBike1, Color colorBike2) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel1 = bikeChannel1;
        this.bikeChannel2 = bikeChannel2;
        this.colorBike1 = colorBike1;
        this.colorBike2 = colorBike2;
    }

    @Override
    public void execute() throws IOException {

    }

    private BigDecimal calculateTotalDistance(List<Bicycle> bikeData) {
        return BigDecimal.ZERO;
    }
}
