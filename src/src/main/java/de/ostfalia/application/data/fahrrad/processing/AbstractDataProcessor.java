package de.ostfalia.application.data.fahrrad.processing;

import de.ostfalia.application.data.entity.Bicycle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractDataProcessor {

    // Diese Methode dient als Template-Methode und ruft die anderen Methoden in der richtigen Reihenfolge auf.
    // Bei dieser klasse werden die anderen Abstrakten gerufen → soll final sein so das man die in anderen Implementationen nicht ändern kann
    // alle anderen Abstrakten werden dann individuell implementiert
    // wollen wir das mit der displayData machen oder an contorller ?
    public final void process() {
        List<Bicycle> bicycles = fetchData();
        List<ProcessedData> processedData = calculateData(bicycles);
        displayData(processedData);
    }

    // Methode zum Abrufen der Daten aus der Datenbank.
    protected abstract List<Bicycle> fetchData();

    // Methode zur Berechnung der gewünschten Metriken (Distanz, Geschwindigkeit, Umdrehungen).
    protected abstract List<ProcessedData> calculateData(List<Bicycle> bicycles);

    // Methode zum Anzeigen der berechneten Daten.
    protected abstract void displayData(List<ProcessedData> processedData);

    // Je nach Anforderungen kann es sinnvoll sein, zusätzliche Methoden für Vor- und Nachverarbeitungsschritte zu definieren.
    // Beispielmethoden dafür könnten sein:
    protected void preprocessData(List<Bicycle> bicycles) {
        // Standardimplementierung oder abstrakte Methode
    }

    protected void postprocessData(List<ProcessedData> processedData) {
        // Standardimplementierung oder abstrakte Methode
    }

    // Diese Methode könnte verwendet werden, um Ergebnisse zu speichern, wenn dies erforderlich ist.
    protected void saveResults(List<ProcessedData> processedData) {
        // Standardimplementierung oder abstrakte Methode
    }

    // Hilfsklassen oder -methoden, um die Verarbeitung zu unterstützen
    // Beispiel für eine Hilfsklasse zur Repräsentation der verarbeiteten Daten
    public class ProcessedData {
        private int channel;
        private BigDecimal value; // Dies könnte Distanz, Geschwindigkeit oder Umdrehungen sein
        private LocalDateTime timestamp;

        public ProcessedData(int c, BigDecimal b, LocalDateTime l) {
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
        // Konstruktor, Getter und Setter hier...
    }

    // Weitere Hilfsmethoden oder Klassen können hier definiert werden...
}

