package de.ostfalia.application.data.fahrrad.processing;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public abstract class AbstractDataProcessor {

    @Autowired
    protected BikeService bikeService;

    private List<ProcessedData> processedData;

    // Verarbeitung basierend auf Start- und Endzeit mit Intervallgröße
    public final void process(int channel, LocalDateTime startTime, LocalDateTime endTime, int intervalInMinutes) {
        List<Bicycle> bicycles = fetchData(channel, startTime, endTime);
        processedData = calculateData(bicycles, intervalInMinutes);
    }

    // Verarbeitung basierend auf Dauer ab jetzt rückwärts mit Intervallgröße
    public final void process(int channel, Duration duration, int intervalInMinutes) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(duration);
        process(channel, startTime, endTime, intervalInMinutes);
    }

    // Verarbeitung basierend auf der letzten Nutzung vor/nach Zeit x mit Intervallgröße
    public final void processSinceLastActivity(int channel, LocalDateTime sinceTime, int intervalInMinutes) {
        List<Bicycle> bicycles = fetchDataSince(channel, sinceTime);
        processedData = calculateData(bicycles, intervalInMinutes);
    }
        


    // Hook-Methoden
    protected abstract List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime);
    protected abstract List<Bicycle> fetchDataSince(int channel, LocalDateTime sinceTime);
    protected abstract LocalDateTime fetchLastActivity(int channel);

    protected abstract List<ProcessedData> calculateData(List<Bicycle> bicycles, int intervalInMinutes);

    public List<ProcessedData> getResults(){
        return processedData;
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

