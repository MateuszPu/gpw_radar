package com.gpw.radar.service.stockTickers;

import com.gpw.radar.Application;
import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.auto.update.stockTickers.StockTickerUpdater;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockBatchWebParser;
import com.gpw.radar.service.parser.web.stock.StockDetailsWebParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StockTickerUpdaterTest {

    private StockTickerUpdater stockTickerUpdater;

    @Inject
    private StockRepository stockRepository;

    @Inject
    @Qualifier("stooqDataParserService")
    private StockDetailsWebParser detailsParser;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private CacheManager cacheManager;

    private StockDetailsWebParser mockStockDetailsWebParser;
    private StockBatchWebParser mockStockBatchWebParser;
    private UrlStreamsGetterService mockUrlStreamsGetterService;
    private Document htmlDoc;

    @Before
    public void init() throws IOException {
        stockRepository.save(StockBuilder.stockBuilder().ticker("abc").build());
        stockRepository.save(StockBuilder.stockBuilder().ticker("def").build());

        String htmlStooqSite = "/stocks_data/stooqSite.html";
        try (InputStream in = getClass().getResourceAsStream(htmlStooqSite)) {
            htmlDoc = Jsoup.parse(in, null, "uri cannot be null");
        }

        mockServices();
    }

    private void mockServices() {
        mockStockDetailsWebParser = Mockito.mock(StockDetailsWebParser.class);
        when(mockStockDetailsWebParser.getStockNameFromWeb(anyObject())).thenReturn("AAA AAA");

        mockStockBatchWebParser = Mockito.mock(StockBatchWebParser.class);
        when(mockStockBatchWebParser.getDocumentForAllStocks()).thenReturn(new Document("dummy"));
        when(mockStockBatchWebParser.fetchAllTickers(anyObject())).thenReturn(new HashSet<>(Arrays.asList("kgh")));

        mockUrlStreamsGetterService = Mockito.mock(UrlStreamsGetterService.class);
        when(mockUrlStreamsGetterService.getDocFromUrl(anyString())).thenReturn(htmlDoc);

        stockTickerUpdater = new StockTickerUpdater(stockRepository, stockDetailsRepository,
            mockStockBatchWebParser, detailsParser, mockUrlStreamsGetterService);
    }

    @Test
    public void stockTickerUpdater() {
        stockTickerUpdater.updateStockTickers();
        cacheManager.getCache(CacheConfiguration.STOCK_TICKERS_CACHE).clear();
        Set<String> allTickers = stockRepository.findAllTickers();

        Stock newStock = stockRepository.findByTicker("kgh");
        assertThat(newStock.getStockName()).isEqualToIgnoringCase("KGHM Polska Miedź SA");
        assertThat(newStock.getStockShortName()).isEqualToIgnoringCase("KGHM");

        StockDetails topByStockOrderByDateDesc = stockDetailsRepository.findTopByStockOrderByDateDesc(newStock);
        assertThat(topByStockOrderByDateDesc.getDate()).isEqualTo(LocalDate.of(2016, 6, 10));
        assertThat(topByStockOrderByDateDesc.getClosePrice()).isEqualTo(new BigDecimal("59.85"));
        assertThat(topByStockOrderByDateDesc.getOpenPrice()).isEqualTo(new BigDecimal("60.67"));
        assertThat(topByStockOrderByDateDesc.getMinPrice()).isEqualTo(new BigDecimal("59.21"));
        assertThat(topByStockOrderByDateDesc.getMaxPrice()).isEqualTo(new BigDecimal("60.90"));
        assertThat(topByStockOrderByDateDesc.getVolume()).isEqualTo(590000l);
        assertThat(allTickers).isEqualTo(new HashSet<>(Arrays.asList("kgh", "abc", "def")));
    }
}
