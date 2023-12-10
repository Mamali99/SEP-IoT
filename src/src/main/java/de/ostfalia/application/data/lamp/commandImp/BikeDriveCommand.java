package de.ostfalia.application.data.lamp.commandImp;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.fahrrad.impl.SpeedDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.lamp.model.Command;
import de.ostfalia.application.data.lamp.service.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

public class BikeDriveCommand implements Command {
    private Java2NodeRedLampAdapter lamp;
    private BikeService bikeService;

    private static final BigDecimal CIRCUMFERENCE = new BigDecimal("2.111");
    private static final BigDecimal ROTATION_DIVISOR = new BigDecimal(4);
    private static final int MAX_INTENSITY = 255;
    private int bikeChannel;
    private static final BigDecimal MAX_SPEED = BigDecimal.valueOf(50); // Maximalgeschwindigkeit laut pdf

    public BikeDriveCommand(Java2NodeRedLampAdapter lamp, BikeService bikeService, int bikeChannel) {
        this.lamp = lamp;
        this.bikeService = bikeService;
        this.bikeChannel = bikeChannel;
    }


    @Override
    public void execute() throws IOException {
        // Create a new instance of SpeedDataProcessor
        SpeedDataProcessor speedDataProcessor = new SpeedDataProcessor(bikeService);

        // Process the data
        speedDataProcessor.process(bikeChannel, Duration.ofMinutes(1), 1);

        // Get the processed data
        List<AbstractDataProcessor.ProcessedData> processedDataList = speedDataProcessor.getResults();

        if (processedDataList == null || processedDataList.isEmpty()) {
            System.out.println("No bicycle activities found for channel " + bikeChannel);
            return;
        }

        BigDecimal averageSpeed = processedDataList.stream()
                .map(AbstractDataProcessor.ProcessedData::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(processedDataList.size()), 2, RoundingMode.HALF_UP);

        BigDecimal speedRatio = averageSpeed.compareTo(MAX_SPEED) > 0 ? BigDecimal.ONE : averageSpeed.divide(MAX_SPEED, 2, RoundingMode.HALF_UP);
        BigDecimal averageSpeedPerSecond = averageSpeed.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        System.out.println("Average speed per second: " + averageSpeedPerSecond);


        int bulbIntensity = speedRatio.multiply(BigDecimal.valueOf(MAX_INTENSITY)).intValue();

        lamp.setIntensity(bulbIntensity);
    }

    @Override
    public void saveCurrentState() {

    }

    @Override
    public void undo() throws IOException {
        System.out.printf("sss");
    }

    private BigDecimal calculateSpeed(List<Bicycle> bikeData) {
        //hardcoded
        BigDecimal rotations = bikeData.get(0).getRotations().divide(ROTATION_DIVISOR, 2, RoundingMode.HALF_UP).multiply(CIRCUMFERENCE);

        BigDecimal timeInterval = BigDecimal.valueOf(60);

        if (timeInterval.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return rotations.divide(timeInterval, 2, RoundingMode.HALF_UP);
        }
    }
}
