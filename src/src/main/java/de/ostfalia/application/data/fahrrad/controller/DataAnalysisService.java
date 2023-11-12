package de.ostfalia.application.data.fahrrad.controller;

import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataAnalysisService {

    public Map<String, BigDecimal> calculateAverageAndSum(List<AbstractDataProcessor.ProcessedData> processedDataList) {
        Map<String, BigDecimal> result = new HashMap<>();

        if (processedDataList == null || processedDataList.isEmpty()) {
            result.put("average", BigDecimal.ZERO);
            result.put("sum", BigDecimal.ZERO);
            return result;
        }

        BigDecimal sum = BigDecimal.ZERO;

        for (AbstractDataProcessor.ProcessedData data : processedDataList) {
            sum = sum.add(data.getValue());
        }

        BigDecimal average = sum.divide(new BigDecimal(processedDataList.size()), 2, RoundingMode.HALF_UP);

        result.put("average", average);
        result.put("sum", sum);

        return result;
    }
}
