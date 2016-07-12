package com.gpw.radar.service.parser.web.stockDetails;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockDetailsParserImpl implements StockDetailsParser {

    private String urlSource = "https://www.gpw.pl/notowania_archiwalne?type=10&date=" + this.date + "&fetch.x=12&fetch.y=16";
    //format YYYY-MM-DD
    private String date = "";

    @Inject
    private StockRepository stockRepository;

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    @Override
    public List<StockDetails> parseStockDetails(LocalDate date) {
        InputStream inputStreamFromUrl = urlStreamsGetterService.getInputStreamFromUrl(urlSource);


        List<Stock> stocks = stockRepository.findAll();

        return null;
    }
}
