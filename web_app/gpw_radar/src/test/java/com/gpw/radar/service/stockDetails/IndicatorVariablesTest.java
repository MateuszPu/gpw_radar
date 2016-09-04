package com.gpw.radar.service.stockDetails;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.auto.update.stockDetails.indicators.IndicatorVariables;
import com.gpw.radar.service.builders.StockDetailsBuilder;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class IndicatorVariablesTest {

    private IndicatorVariables indicatorVariables;

    @Before
    public void init() {
        List<StockDetails> details = new LinkedList<>();
        details.add(StockDetailsBuilder.stockDetailsBuilder().date(LocalDate.of(2016, 1, 1)).volume(123l).closePrice("100").build());
        details.add(StockDetailsBuilder.stockDetailsBuilder().date(LocalDate.of(2015, 1, 1)).volume(456l).closePrice("99").build());
        details.add(StockDetailsBuilder.stockDetailsBuilder().date(LocalDate.of(2014, 1, 1)).volume(234l).closePrice("87").build());
        details.add(StockDetailsBuilder.stockDetailsBuilder().date(LocalDate.of(2018, 1, 1)).volume(652l).closePrice("105.25").build());
        this.indicatorVariables = new IndicatorVariables(details);
    }

    @Test
    public void orderOfStockDetailsTest() {
        List<StockDetails> stockDetails = indicatorVariables.getStockDetails();
        assertThat(stockDetails.get(0).getDate().getYear()).isEqualTo(2018);
        assertThat(stockDetails.get(stockDetails.size() - 1).getDate().getYear()).isEqualTo(2014);

    }

    @Test
    public void percentReturnTest() {
        BigDecimal percentReturn = indicatorVariables.calculatePercentReturn();
        assertThat(percentReturn).isEqualTo(new BigDecimal("5.25"));
    }

    @Test
    public void averageVolumeTest() {
        BigDecimal averageVolume1 = indicatorVariables.calculateAverageVolume(4);
        assertThat(averageVolume1).isEqualTo(new BigDecimal("366.25"));

        BigDecimal averageVolume2 = indicatorVariables.calculateAverageVolume(6);
        assertThat(averageVolume2).isEqualTo("0.00");
    }

    @Test
    public void volumeRatioTest() {
        BigDecimal volumeRatio1 = indicatorVariables.calculateVolumeRatio(4);
        assertThat(volumeRatio1).isEqualTo(new BigDecimal("1.78"));

        BigDecimal volumeRatio2 = indicatorVariables.calculateVolumeRatio(6);
        assertThat(volumeRatio2).isEqualTo("0.00");
    }

    @Test
    public void averageVolumeValueTest() {
        BigDecimal averageVolumeValue1 = indicatorVariables.calculateAverageVolumeValue(4);
        assertThat(averageVolumeValue1).isEqualTo(new BigDecimal("38547.81"));

        BigDecimal averageVolumeValue2 = indicatorVariables.calculateAverageVolumeValue(6);
        assertThat(averageVolumeValue2).isEqualTo("0.00");
    }

    @Test
    public void slopeSimpleRegressionTest() {
        double slopeSimpleRegressionValue1 = indicatorVariables.calculateSlopeSimpleRegression(4);
        assertThat(slopeSimpleRegressionValue1).isCloseTo(35.61, Percentage.withPercentage(0.5));

        double slopeSimpleRegressionValue2 = indicatorVariables.calculateSlopeSimpleRegression(6);
        assertThat(slopeSimpleRegressionValue2).isEqualTo(0);
    }
}
