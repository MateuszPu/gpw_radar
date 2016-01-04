package com.gpw.radar.service.database;

import com.gpw.radar.domain.database.DailyStockDetailsParser;
import com.gpw.radar.domain.database.FillDataStatus;
import com.gpw.radar.domain.database.Type;
import com.gpw.radar.repository.auto.update.DailyStockDetailsParserRepository;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfiguratorService {

    @Inject
    private DailyStockDetailsParserRepository dailyStockDetailsParserRepository;

    @Inject
    private FillDataStatusRepository fillDataStatusRepository;

    @Inject
    private FillDataBaseWithDataService fillDataBaseWithDataService;

    public void changeStockDetailsParserMethod(ParserMethod stockDetailsParserMethod) {
        dailyStockDetailsParserRepository.setStockDetailsParserMethod(stockDetailsParserMethod.toString());
    }

    public DailyStockDetailsParser getCurrentStockDetailsParserMethod() {
        DailyStockDetailsParser currentMethod = dailyStockDetailsParserRepository.findMethod();
        return currentMethod;
    }

    @Transactional
    public ResponseEntity<Void> setParserMethod(ParserMethod parserMethod) {
        dailyStockDetailsParserRepository.setStockDetailsParserMethod(parserMethod.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<List<FillDataStatus>> getFillDataStatus() {
        List<FillDataStatus> list = fillDataStatusRepository.findAll();
        return new ResponseEntity<List<FillDataStatus>>(list, HttpStatus.OK);
    }
}
