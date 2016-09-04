package com.gpw.radar.service.stockFiveMinutesDetails;


import com.gpw.radar.Application;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.file.stockFiveMinutesDetails.FileStockFiveMinutesDetailsParserService;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@RunWith(Parameterized.class)
public class CalculateIndicatorsFiveMinutesStockDetailsTest {

    @Inject
    private FileStockFiveMinutesDetailsParserService fileStockFiveMinutesDetailsParserService;

    private double expectedResult;
    private LocalTime time;
    private Percentage perc = Percentage.withPercentage(0.05);
    private List<StockFiveMinutesIndicators> indicatorsList;

    @Before
    public void init() throws Exception {
        TestContextManager testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
        Stock stock = StockBuilder.sampleStock().ticker("kgh").stockName("KGH").stockShortName("KGH").build();
        String stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/" + stock.getTicker() + ".txt";
        InputStream inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        List<StockFiveMinutesDetails> stockFiveMinutesDetails = fileStockFiveMinutesDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledStockFiveMinutesDetails = fileStockFiveMinutesDetailsParserService.fillEmptyTimeAndCumulativeVolume(stockFiveMinutesDetails);
        indicatorsList = fileStockFiveMinutesDetailsParserService.calculateIndicatorsFromDetails(filledStockFiveMinutesDetails);
    }

    public CalculateIndicatorsFiveMinutesStockDetailsTest(LocalTime time, double expectedResult) {
        this.time = time;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][]{
            {LocalTime.of(9, 05), 57965.00},
            {LocalTime.of(10, 00), 293880.81},
            {LocalTime.of(11, 00), 430844.90},
            {LocalTime.of(12, 20), 564788},
            {LocalTime.of(13, 00), 625414},
            {LocalTime.of(14, 30), 796119.09},
            {LocalTime.of(15, 00), 874832.18},
            {LocalTime.of(16, 15), 1080512.63}
        });
    }

    @Test
    public void stockFiveMinuteIndicator() {
        double average = indicatorsList.stream().filter(indi -> indi.getTime().equals(time)).findAny().get().getAverageVolume();
        assertThat(average).isCloseTo(expectedResult, perc);
    }

}
