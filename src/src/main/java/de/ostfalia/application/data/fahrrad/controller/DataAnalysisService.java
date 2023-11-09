package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Service
public class DataAnalysisService {

    public AnalysisResult analyze(List<AbstractDataProcessor.ProcessedData> processedData) {
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal average = BigDecimal.ZERO;

        if (!processedData.isEmpty()) {
            for (AbstractDataProcessor.ProcessedData data : processedData) {
                sum = sum.add(data.getValue());
            }
            average = sum.divide(BigDecimal.valueOf(processedData.size()), 2, RoundingMode.HALF_UP);
        }

        return new AnalysisResult(sum, average);
    }

    public static class AnalysisResult {
        private final BigDecimal sum;
        private final BigDecimal average;

        public AnalysisResult(BigDecimal sum, BigDecimal average) {
            this.sum = sum;
            this.average = average;
        }

        public BigDecimal getSum() {
            return sum;
        }

        public BigDecimal getAverage() {
            return average;
        }
    }
}