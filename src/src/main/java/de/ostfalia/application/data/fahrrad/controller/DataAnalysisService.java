package de.ostfalia.application.data.fahrrad.controller;


import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DataAnalysisService {

    public static class AnalysisResult {
        private BigDecimal average;
        private BigDecimal sum;

        public AnalysisResult(BigDecimal average, BigDecimal sum) {
            this.average = average;
            this.sum = sum;
        }

        public BigDecimal getAverage() {
            return average;
        }

        public BigDecimal getSum() {
            return sum;
        }
    }

    public AnalysisResult calculateAverageAndSum(List<AbstractDataProcessor.ProcessedData> processedDataList) {
        if (processedDataList == null || processedDataList.isEmpty()) {
            return new AnalysisResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal lastValue = processedDataList.get(processedDataList.size() - 1).getValue();

        BigDecimal firstValue = processedDataList.get(0).getValue();

        BigDecimal sum = firstValue.subtract(lastValue).abs();



        BigDecimal average = sum.divide(new BigDecimal(processedDataList.size()), 2, RoundingMode.HALF_UP);

        AnalysisResult a = new AnalysisResult(average, sum);
        System.out.println("Average: " + a.getAverage());
        System.out.println("Sum: " + a.getSum());

        return a;
    }
}
