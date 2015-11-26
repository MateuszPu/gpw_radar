package com.gpw.radar.service;


import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.service.database.FillDataBaseWithDataService;
import com.gpw.radar.service.parser.file.StockDetailsParserService;
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
public class fillDataBaseWithDataServiceTest {

    @Inject
    private FillDataBaseWithDataService fillDataBaseWithDataService;

    @Inject
    private StockDetailsParserService stockDetailsParserService;

    private TestContextManager testContextManager;

    private double expectedResult;
    private LocalTime time;
    private Percentage perc = Percentage.withPercentage(0.5);
    private List<StockFiveMinutesIndicators> indicatorsList;

    @Before
    public void setup() throws Exception {
        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);
        Stock stock = new Stock();
        stock.setTicker(StockTicker.kgh);
        stock.setStockName("KGH");
        stock.setStockShortName("KGH");
        String stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        InputStream inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledStockFiveMinutesDetails = stockDetailsParserService.fillEmptyTimeAndCumulativeVolume(stockFiveMinutesDetails);
        indicatorsList = fillDataBaseWithDataService.calculateIndicatorsFromDetails(filledStockFiveMinutesDetails);
    }

    public fillDataBaseWithDataServiceTest(LocalTime time, double expectedResult) {
        this.time = time;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
            { LocalTime.of(9,05), 57965.00 },
            { LocalTime.of(10,00), 293880.81 },
            { LocalTime.of(11,00), 430844.90 },
            { LocalTime.of(12,20), 564788 },
            { LocalTime.of(13,00), 625414 },
            { LocalTime.of(14,30), 796119.09},
            { LocalTime.of(15,00), 874832.18 },
            { LocalTime.of(16, 15), 1080512.63 }
        });
    }

    @Test
    public void stockFiveMinuteIndicator() {
        double average = indicatorsList.stream().filter(indi -> indi.getTime().equals(time)).findAny().get().getAverageVolume();
        assertThat(average).isCloseTo(expectedResult, perc);
    }

}
