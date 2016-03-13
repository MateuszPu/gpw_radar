package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.auto.update.DailyStockDetailsParserRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StockIndicatorsCalculator;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.stock.StockDetailsService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//@RestController
//@RequestMapping("/api")
@RolesAllowed(AuthoritiesConstants.ADMIN)
@Service
public class StocksDetailsUpdater {

	@Inject
	private StockDetailsService stockDetailsService;

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

	private StockDetailsParser stockDetailsParser;
	private StockIndicatorsCalculator stockIndicatorsCalculator;


//	@RequestMapping(value = "/update/db", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Transactional
	@Scheduled(cron = "0 30 17 ? * MON-FRI")
	public void updateStockDetails() throws IOException, InterruptedException {
		LocalDate lastQuotedDateFromDataBase = stockDetailsService.findLastTopDate().getBody();
		LocalDate lastQuotedDateFromStooqWeb = dateAndTimeParserService.getLastDateWig20FromStooqWebsite();
		stockIndicatorsCalculator = beanFactory.getBean("standardStockIndicatorsCalculator", StockIndicatorsCalculator.class);

		switch (configuratorRepository.findMethod().getParserMethod()) {
		case GPW:
			stockDetailsParser = beanFactory.getBean("gpwParser", StockDetailsParser.class);
			break;
		case STOOQ:
			stockDetailsParser = beanFactory.getBean("stooqParser", StockDetailsParser.class);
			stockDetailsParser.setQuotesDate(lastQuotedDateFromStooqWeb);
			break;
		}

		if (!lastQuotedDateFromDataBase.isEqual(lastQuotedDateFromStooqWeb)) {
            List<StockDetails> currentStockDetails = stockDetailsParser.getCurrentStockDetails();
            stockDetailsRepository.save(currentStockDetails);

            List<StockIndicators> stockIndicators = stockIndicatorsCalculator.calculateCurrentStockIndicators();
            stockIndicatorsRepository.save(stockIndicators);
		}
	}
}
