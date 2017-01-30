package com.gpw.radar.service.stock;

import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StooqDataParserServiceData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StockServiceTest {

    private final StockRepository stockRepositoryMock = Mockito.mock(StockRepository.class);
    private final UrlStreamsGetterService urlStreamsGetterServiceMock = Mockito.mock(UrlStreamsGetterService.class);
    private final StockIndicatorsRepository stockIndicatorsRepositoryMock = Mockito.mock(StockIndicatorsRepository.class);
    private final StockMapperDtoFacade stockMapperDtoFacade = new StockMapperDtoFacade();
    private Stock pzu;
    private Stock tpe;
    private Document stooqSite;
    private StockService objectUnderTest;

    @Before
    public void init() throws IOException {
        mockStocks();
        mockStooqSite();
        objectUnderTest = new StockService(stockRepositoryMock, new StooqDataParserServiceData(),
            urlStreamsGetterServiceMock, stockIndicatorsRepositoryMock, stockMapperDtoFacade);
    }

    private void mockStooqSite() throws IOException {
        String htmlStooqSite = "/stocks_data/stooqSite.html";
        try (InputStream in = getClass().getResourceAsStream(htmlStooqSite)) {
            stooqSite = Jsoup.parse(in, null, "uri cannot be null");
        }
    }

    private void mockStocks() {
        pzu = StockBuilder.sampleStock().id("1").stockName("PZU UBEZPIECZENIA").stockShortName("PZU SA").ticker("pzu").build();
        tpe = StockBuilder.sampleStock().id("2").stockName("TAURON ENERGIA").stockShortName("TAURON").ticker("tpe").build();
    }

    @Test
    public void shouldCreateMissingStock() {
        //given
        given(urlStreamsGetterServiceMock.getDocFromUrl(anyObject())).willReturn(stooqSite);
        given(stockRepositoryMock.findByTicker("pzu")).willReturn(pzu);
        given(stockRepositoryMock.findByTicker("tpe")).willReturn(tpe);
        List<StockDetails> stocksDetailsToProcessMissingData = prepareStockDetails();

        //when
        stocksDetailsToProcessMissingData.forEach(e -> objectUnderTest.addMissingData(e));

        //then
        verify(stockRepositoryMock, times(1)).save(any(Stock.class));
    }

    @Test
    public void shouldReturnStockDetailsFilledWithCorrectStockName() {
        //given
        given(stockRepositoryMock.findByTicker("pzu")).willReturn(pzu);
        given(stockRepositoryMock.findByTicker("tpe")).willReturn(tpe);

        //when
        StockDetails tpeStockDetails = new StockDetails();
        tpeStockDetails.setStockWith("tpe", null, "TAURON");
        StockDetails stockDetails = objectUnderTest.addMissingData(tpeStockDetails);

        //then
        assertThat(stockDetails.getStock().getTicker()).isEqualTo("tpe");
        assertThat(stockDetails.getStock().getName()).isEqualTo("TAURON ENERGIA");
        assertThat(stockDetails.getStock().getShortName()).isEqualTo("TAURON");
    }

    @Test
    public void shouldCallStocksIndicators10DaysTrendUpMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.UP;
        int days = 10;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators10DaysTrendUp(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators10DaysTrendUp(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators30DaysTrendUpMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.UP;
        int days = 30;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators30DaysTrendUp(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators30DaysTrendUp(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators60DaysTrendUpMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.UP;
        int days = 60;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators60DaysTrendUp(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators60DaysTrendUp(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators90DaysTrendUpMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.UP;
        int days = 90;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators90DaysTrendUp(any(), any())).willReturn(resultMock);

        //when
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators90DaysTrendUp(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators10DaysTrendDownMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.DOWN;
        int days = 10;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators10DaysTrendDown(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators10DaysTrendDown(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators30DaysTrendDownMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.DOWN;
        int days = 30;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators30DaysTrendDown(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators30DaysTrendDown(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators60DaysTrendDownMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.DOWN;
        int days = 60;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators60DaysTrendDown(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators60DaysTrendDown(any(), any());
    }

    @Test
    public void shouldCallStocksIndicators90DaysTrendDownMethod() throws URISyntaxException {
        //given
        LocalDate notImportantDate = LocalDate.of(2016, 1, 1);
        TrendDirection trendDirection = TrendDirection.DOWN;
        int days = 90;
        Page<StockIndicators> resultMock = new PageImpl<>(new ArrayList<>());
        given(stockIndicatorsRepositoryMock.findWithStocksIndicators90DaysTrendDown(any(), any())).willReturn(resultMock);

        //then
        objectUnderTest.getTrendingStocks(notImportantDate, trendDirection, days, 0, 5);

        //then
        verify(stockIndicatorsRepositoryMock, times(1)).findWithStocksIndicators90DaysTrendDown(any(), any());
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
