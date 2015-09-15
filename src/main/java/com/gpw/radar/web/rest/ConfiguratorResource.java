package com.gpw.radar.web.rest;

import java.util.EnumSet;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.service.ConfiguratorService;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

@RestController
@RequestMapping("/api/configurator")
public class ConfiguratorResource {
	
	@Inject
	private ConfiguratorService configuratorService;
	
	@RequestMapping(value = "all/stock/details/parser/methods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnumSet<ParserMethod>> getAllMethods() {
        return new ResponseEntity<EnumSet<ParserMethod>>(EnumSet.allOf(ParserMethod.class), HttpStatus.OK);
    }
	
	@RequestMapping(value = "current/stock/details/parser/method", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ParserMethodTemp> getCurrentMethod() {
		ParserMethod currentMethod = configuratorService.getCurrentStockDetailsParserMethod();
		return new ResponseEntity<ParserMethodTemp>(new ParserMethodTemp(currentMethod), HttpStatus.OK);
	}
	
	@RequestMapping(value = "set/stock/details/parser/method", method = RequestMethod.GET)
	public ResponseEntity<Void> setParserMethod(@RequestParam ParserMethod parserMethod){
		return configuratorService.setParserMethod(parserMethod);
	}
	
	private class ParserMethodTemp {
		private ParserMethod parserMethod;
		
		public ParserMethodTemp(ParserMethod parserMethod){
			this.parserMethod = parserMethod;
		}
		
		public ParserMethod getParserMethod() {
			return parserMethod;
		}
		public void setParserMethod(ParserMethod parserMethod) {
			this.parserMethod = parserMethod;
		}
	}
}
