package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.auto.update.DailyStockDetailsParserRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StockIndicatorsCalculator;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RolesAllowed(AuthoritiesConstants.ADMIN)
@Service
public class StocksDetailsUpdater {

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private DailyStockDetailsParserRepository configuratorRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockIndicatorsRepository stockIndicatorsRepository;

    @Inject
    private BeanFactory beanFactory;

    @Transactional
    @Scheduled(cron = "0 30 17 ? * MON-FRI")
    @CacheEvict(cacheNames = {CacheConfiguration.TRENDING_STOCKS_CACHE, CacheConfiguration.STOCK_DETAILS_BY_TICKER_CACHE,
        CacheConfiguration.ALL_STOCKS_FETCH_INDICATORS_CACHE, CacheConfiguration.LAST_QUTED_DATE}, allEntries = true)
    public void updateStockDetails() throws IOException, InterruptedException {
        LocalDate lastQuotedDateFromDataBase = stockDetailsRepository.findTopDate();
        LocalDate lastQuotedDateFromStooqWeb = dateAndTimeParserService.getLastDateWig20FromStooqWebsite();
        StockIndicatorsCalculator stockIndicatorsCalculator = beanFactory.getBean("standardStockIndicatorsCalculator", StockIndicatorsCalculator.class);
        StockDetailsParser stockDetailsParser;

        switch (configuratorRepository.findMethod().getParserMethod()) {
            case GPW:
                stockDetailsParser = beanFactory.getBean("gpwParser", StockDetailsParser.class);
                break;
            case STOOQ:
                stockDetailsParser = beanFactory.getBean("stooqParser", StockDetailsParser.class);
                stockDetailsParser.setQuotesDate(lastQuotedDateFromStooqWeb);
                break;
            default:
                throw new IllegalStateException("No case handle in switch case");
        }

        if (!lastQuotedDateFromDataBase.isEqual(lastQuotedDateFromStooqWeb)) {
            List<StockDetails> currentStockDetails = stockDetailsParser.getCurrentStockDetails();
            stockDetailsRepository.save(currentStockDetails);

            List<StockIndicators> stockIndicators = stockIndicatorsCalculator.calculateCurrentStockIndicators();
            stockIndicatorsRepository.save(stockIndicators);
        }
    }
}
