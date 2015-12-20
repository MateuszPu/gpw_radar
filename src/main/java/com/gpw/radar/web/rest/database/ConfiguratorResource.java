package com.gpw.radar.web.rest.database;

import com.gpw.radar.domain.database.DailyStockDetailsParser;
import com.gpw.radar.domain.database.FillDataStatus;
import com.gpw.radar.domain.database.Type;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;
import com.gpw.radar.service.database.ConfiguratorService;
import com.gpw.radar.service.database.FillDataBaseWithDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/configurator")
@RolesAllowed(AuthoritiesConstants.ADMIN)
public class ConfiguratorResource {

	@Inject
	private ConfiguratorService configuratorService;

	@Inject
	private FillDataBaseWithDataService fillDataBaseWithDataService;

	@RequestMapping(value = "all/stock/details/parser/methods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnumSet<ParserMethod>> getAllMethods() {
		return new ResponseEntity<EnumSet<ParserMethod>>(EnumSet.allOf(ParserMethod.class), HttpStatus.OK);
	}

    @RequestMapping(value = "current/stock/details/parser/method", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DailyStockDetailsParser> getCurrentMethod() {
		DailyStockDetailsParser currentMethod = configuratorService.getCurrentStockDetailsParserMethod();
		return new ResponseEntity<DailyStockDetailsParser>(currentMethod, HttpStatus.OK);
	}

	@RequestMapping(value = "set/stock/details/parser/method", method = RequestMethod.GET)
	public ResponseEntity<Void> setParserMethod(@RequestParam ParserMethod parserMethod) {
		return configuratorService.setParserMethod(parserMethod);
	}

	@RequestMapping(value = "get/fill/data/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FillDataStatus>> getFillDataStatus() {
		return configuratorService.getFillDataStatus();
	}

	@RequestMapping(value = "fill/database", method = RequestMethod.GET)
	public ResponseEntity<Void> fillDatabaseWithData(@RequestParam Type type) {
		return fillDataBaseWithDataService.fillDatabaseWithData(type);
	}

	@RequestMapping(value = "get/step/of/fill", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getStepOfFill() {
		return new ResponseEntity<Integer>(fillDataBaseWithDataService.getStep(), HttpStatus.OK);
	}
}
