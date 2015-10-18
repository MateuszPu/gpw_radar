package com.gpw.radar.service.auto.update;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.repository.auto.update.DailyStockDetailsParserRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.auto.update.stockDetails.StockDetailsParser;
import com.gpw.radar.service.auto.update.stockIndicators.StockIndicatorsCalculator;
import com.gpw.radar.service.database.WebParserService;
import com.gpw.radar.service.stock.StockDetailsService;

@RestController
@RequestMapping("/api")
@RolesAllowed(AuthoritiesConstants.ADMIN)
public class AutoUpdateStocksData {

	@Inject
	private StockDetailsService stockDetailsService;

	@Inject
	private WebParserService webParserService;

	// @Inject
	// private ApplicationStatus applicationStatus;

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

	// @RequestMapping(value = "/status/step", method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public int getStepStatus() {
	// return applicationStatus.getStep();
	// }
	//
	// @RequestMapping(value = "/is/updating", method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public boolean isApplicationUpdating() {
	// return applicationStatus.isUpdating();
	// }

	@RequestMapping(value = "/update/db", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
//	@Scheduled(cron = "0 30 17 ? * MON-FRI")
	public void updateStockDetails() throws IOException, InterruptedException {
		LocalDate lastQuotedDateFromDataBase = stockDetailsService.findLastTopDate().getBody();
		LocalDate lastQuotedDateFromStooqWeb = webParserService.getLastDateWig20FromStooqWebsite();
		stockIndicatorsCalculator = beanFactory.getBean("standardStockIndicatorsCalculator", StockIndicatorsCalculator.class);

		switch (configuratorRepository.findMethod().getParserMethod()) {
		case GPW:
			stockDetailsParser = beanFactory.getBean("gpwParser", StockDetailsParser.class);
			break;
		case STOOQ:
			stockDetailsParser = beanFactory.getBean("stooqParser", StockDetailsParser.class);
			stockDetailsParser.setQutesDate(lastQuotedDateFromStooqWeb);
			break;
		}

		if (!lastQuotedDateFromDataBase.isEqual(lastQuotedDateFromStooqWeb)) {
			stockDetailsRepository.save(stockDetailsParser.getCurrentStockDetails());
			stockIndicatorsRepository.save(stockIndicatorsCalculator.calculateCurrentStockIndicators());
		}
	}
}