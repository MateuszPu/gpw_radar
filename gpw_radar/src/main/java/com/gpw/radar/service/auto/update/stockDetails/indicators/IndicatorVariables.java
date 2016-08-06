package com.gpw.radar.service.auto.update.stockDetails.indicators;

import com.gpw.radar.domain.stock.StockDetails;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IndicatorVariables {

    private final LocalDate date;
    private final List<StockDetails> stockDetails;
    private final BigDecimal multiplicand = new BigDecimal(100);

    public IndicatorVariables(List<StockDetails> stockDetails) {
        this.stockDetails = stockDetails.stream()
            .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
            .collect(Collectors.toList());

        if (this.stockDetails.get(0).getVolume() != 0) {
            this.date = this.stockDetails.get(0).getDate();
        } else {
            date = null;
        }
    }


    public BigDecimal calculatePercentReturn() {
        BigDecimal result = BigDecimal.ZERO;
        if(stockDetails.size() > 1) {
            BigDecimal todayClose = stockDetails.get(0).getClosePrice();
            BigDecimal previousClose = stockDetails.get(1).getClosePrice();
            BigDecimal divide = todayClose.divide(previousClose, 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal subtract = divide.subtract(BigDecimal.ONE);
            result = subtract.multiply(multiplicand);
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAverageVolume(int period) {
        BigDecimal result = BigDecimal.ZERO;
        if (period <= stockDetails.size()) {
            List<Long> volumesForPeriod = stockDetails.stream()
                .map(e -> e.getVolume())
                .limit(period)
                .collect(Collectors.toList());
            Long sum = volumesForPeriod.stream().mapToLong(e -> e).sum();
            result = new BigDecimal(sum).divide(new BigDecimal(period), 6, BigDecimal.ROUND_HALF_UP);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateVolumeRatio(int period) {
        BigDecimal result = BigDecimal.ZERO;
        if (period <= stockDetails.size()) {
            BigDecimal averageVolume = calculateAverageVolume(period);
            BigDecimal currentVolume = new BigDecimal(stockDetails.get(0).getVolume());
            result = currentVolume.divide(averageVolume, 6, BigDecimal.ROUND_HALF_UP);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAverageVolumeValue(int period) {
        BigDecimal result = BigDecimal.ZERO;
        if (period <= stockDetails.size()) {
            BigDecimal averageVolume = calculateAverageVolume(period);
            result = stockDetails.get(0).getClosePrice().multiply(averageVolume);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public double calculateSlopeSimpleRegression(int period) {
        double result = 0.0;
        if (period <= stockDetails.size()) {
            double[] dataForTrend = normalizeArray(stockDetails.stream()
                .map(e -> e.getClosePrice())
                .limit(period)
                .collect(Collectors.toList()));

            SimpleRegression simpleRegression = new SimpleRegression();

            for (int i = 0; i < dataForTrend.length - 1; i++) {
                simpleRegression.addData(i, dataForTrend[i]);
            }
            result = simpleRegression.getSlope();
        }

        return result;
    }

    private double[] normalizeArray(List<BigDecimal> closePrices) {
        double[] normalized = new double[closePrices.size()];
        BigDecimal max = new BigDecimal(closePrices.stream().mapToDouble(e->e.doubleValue()).max().getAsDouble());
        BigDecimal min = new BigDecimal(closePrices.stream().mapToDouble(e->e.doubleValue()).min().getAsDouble());

        for (int i = 0; i < closePrices.size(); i++) {
            normalized[i] = (normalizeData(closePrices.get(i), max, min)).doubleValue();
        }
        ArrayUtils.reverse(normalized);

        return normalized;
    }

    private BigDecimal normalizeData(BigDecimal number, BigDecimal max, BigDecimal min) {
        BigDecimal subtractNumberMin = number.subtract(min);
        BigDecimal subtractMaxMin = max.subtract(min);
        BigDecimal result = subtractNumberMin.divide(subtractMaxMin, 6, BigDecimal.ROUND_HALF_UP).multiply(multiplicand);

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public List<StockDetails> getStockDetails() {
        return new LinkedList<>(stockDetails);
    }

    public LocalDate getDate() {
        return date;
    }
}
