package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import com.gpw.radar.service.parser.web.stock.StooqDataParserServiceData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class StockServiceTest {


    private StockRepository stockRepositoryMock;
    private UrlStreamsGetterService urlStreamsGetterServiceMock;
    private StockService objectUnderTest;

    @Before
    public void init() throws IOException {
        mockStockRepository();
        mockUrlStreamsGetterService();
        StockDataDetailsWebParser stockDataDetailsWebParser = new StooqDataParserServiceData();
        objectUnderTest = new StockService(stockRepositoryMock, stockDataDetailsWebParser,
            urlStreamsGetterServiceMock, null);
    }

    private void mockStockRepository() {
        stockRepositoryMock = Mockito.mock(StockRepository.class);
        Stock pzu = StockBuilder.sampleStock().id("1").stockName("PZU UBEZPIECZENIA").stockShortName("PZU SA").ticker("pzu").build();
        Stock tpe = StockBuilder.sampleStock().id("2").stockName("TAURON ENERGIA").stockShortName("TAURON").ticker("tpe").build();
        when(stockRepositoryMock.findByTicker("pzu")).thenReturn(pzu);
        when(stockRepositoryMock.findByTicker("tpe")).thenReturn(tpe);
    }

    private void mockUrlStreamsGetterService() throws IOException {
        urlStreamsGetterServiceMock = Mockito.mock(UrlStreamsGetterService.class);
        String htmlStooqSite = "/stocks_data/stooqSite.html";
        try (InputStream in = getClass().getResourceAsStream(htmlStooqSite)) {
            Document stooqSite = Jsoup.parse(in, null, "uri cannot be null");
            when(urlStreamsGetterServiceMock.getDocFromUrl(anyObject())).thenReturn(stooqSite);
        }
    }

    @Test
    public void shouldCreateMissingStock() {
        List<StockDetails> dataToProcess = prepareStockDetails();
        dataToProcess.forEach(e -> objectUnderTest.addMissingData(e));
        verify(stockRepositoryMock, times(1)).save(any(Stock.class));
    }

    @Test
    public void shouldReturnStockDetailsFilledWithStockData() {
        StockDetails tpeStockDetails = new StockDetails();
        tpeStockDetails.setStockWith("tpe", "TAURON ENERGIA", "TAURON");
        StockDetails stockDetails = objectUnderTest.addMissingData(tpeStockDetails);

        assertThat(stockDetails.getStock().getTicker()).isEqualTo("tpe");
        assertThat(stockDetails.getStock().getName()).isEqualTo("TAURON ENERGIA");
        assertThat(stockDetails.getStock().getShortName()).isEqualTo("TAURON");
    }

    private List<StockDetails> prepareStockDetails() {
        List<StockDetails> result = new ArrayList<>();
        StockDetails kghStockDetails = new StockDetails();
        kghStockDetails.setStockWith("kgh", "KGH POLSKA MIEDZ", "KGHM");
        StockDetails tpeStockDetails = new StockDetails();
        tpeStockDetails.setStockWith("tpe", "TAURON ENERGIA", "TAURON");
        StockDetails pzuStockDetails = new StockDetails();
        pzuStockDetails.setStockWith("pzu", "PZU UBEZPIECZENIA", "PZU SA");

        result.add(kghStockDetails);
        result.add(tpeStockDetails);
        result.add(pzuStockDetails);
        return result;
    }
}
