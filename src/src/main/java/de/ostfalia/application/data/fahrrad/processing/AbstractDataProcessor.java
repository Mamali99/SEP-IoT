package de.ostfalia.application.data.fahrrad.processing;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public abstract class AbstractDataProcessor {

    private List<ProcessedData> processedData;


    // Diese Methode dient als Template-Methode und ruft die anderen Methoden in der richtigen Reihenfolge auf.
    public final void process(int channel, LocalDateTime startTime, LocalDateTime endTime) {
        List<Bicycle> bicycles = fetchData(channel, startTime, endTime);
        processedData = calculateData(bicycles);
    }

    // Methode zum Abrufen der Daten aus der Datenbank.
    protected abstract List<Bicycle> fetchData(int channel, LocalDateTime startTime, LocalDateTime endTime);

    // Methode zur Berechnung der gewünschten Metriken (Distanz, Geschwindigkeit, Umdrehungen).
    protected abstract List<ProcessedData> calculateData(List<Bicycle> bicycles);

    public List<ProcessedData> getResults(){
        return processedData;
    }


    // Hilfsklassen oder -methoden, um die Verarbeitung zu unterstützen
    // Beispiel für eine Hilfsklasse zur Repräsentation der verarbeiteten Daten
    public class ProcessedData {
        private int channel;
        private BigDecimal value; // Dies könnte Distanz, Geschwindigkeit oder Umdrehungen sein
        private LocalDateTime timestamp;
        public ProcessedData(int c, BigDecimal b, LocalDateTime l){
            this.channel = c;
            this.value = b;
            this.timestamp = l;
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

    }

}

