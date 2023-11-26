package de.ostfalia.application.data.fahrrad.processing;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public abstract class AbstractDataProcessor {

    @Autowired
    protected BikeService bikeService;

    private List<ProcessedData> processedData;

    private boolean shouldSmoothData = false;

    // Verarbeitung basierend auf Start- und Endzeit mit Intervallgröße
    public final void process(int channel, LocalDateTime startTime, LocalDateTime endTime, int intervalInSeconds) {
        List<Bicycle> bicycles = fetchData(channel, startTime, endTime);
        if(isShouldSmoothData()){
            List<Bicycle> bicycleListSmooth = smoothData(bicycles, 3);
            processedData = calculateData(bicycleListSmooth, intervalInSeconds);
            return;
        }

        processedData = calculateData(bicycles, intervalInSeconds);
    }

    // Verarbeitung basierend auf Dauer ab jetzt rückwärts mit Intervallgröße
    public final void process(int channel, Duration duration, int intervalInSeconds) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(duration);
        process(channel, startTime, endTime, intervalInSeconds);
    }

    // Verarbeitung basierend auf der letzten Nutzung vor/nach Zeit x mit Intervallgröße
    public final void process(int channel, int intervalInSeconds) {
        List<Bicycle> bicycles = fetchLastActivity(channel);
        if(shouldSmoothData){
            List<Bicycle> bicycleListSmooth = smoothData(bicycles, 3);
            processedData = calculateData(bicycleListSmooth, intervalInSeconds);
            return;
        }
        processedData = calculateData(bicycles, intervalInSeconds);

    }

    protected List<Bicycle> smoothData(List<Bicycle> rawBicycles, int windowSize) {
        List<Bicycle> smoothedBicycles = new ArrayList<>();
        Queue<BigDecimal> rotationWindow = new LinkedList<>();

        for (Bicycle bike : rawBicycles) {
            rotationWindow.add(bike.getRotations());
            if (rotationWindow.size() > windowSize) {
                rotationWindow.remove();
            }

            BigDecimal sum = rotationWindow.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal averageRotations = sum.divide(BigDecimal.valueOf(rotationWindow.size()), RoundingMode.HALF_UP);

            Bicycle smoothedBike = new Bicycle();
            smoothedBike.setChannel(bike.getChannel());
            smoothedBike.setTime(bike.getTime());
            smoothedBike.setRotations(averageRotations);
            smoothedBicycles.add(smoothedBike);
        }

        return smoothedBicycles;
    }



    protected abstract List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime);

    protected abstract List<Bicycle> fetchLastActivity(int channel);

    protected abstract List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInSeconds);

    public List<ProcessedData> getResults(){
        return processedData;
    }

    public boolean isShouldSmoothData() {
        return shouldSmoothData;
    }

    public void setShouldSmoothData(boolean shouldSmoothData) {
        this.shouldSmoothData = shouldSmoothData;
    }

    public class ProcessedData {
        private int channel;
        private BigDecimal value; // Dies könnte Distanz, Geschwindigkeit oder Umdrehungen sein
        private LocalDateTime timestamp;

        private String processorName;
        public ProcessedData(int c, BigDecimal b, LocalDateTime l, String processorName){
            this.channel = c;
            this.value = b;
            this.timestamp = l;
            this.processorName = processorName;
        }


        //Hook Methoden
        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }


        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getProcessorName() {
            return processorName;
        }


    }

}

