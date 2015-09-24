package com.gpw.radar.domain.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

@Entity(name = "STOCK_DETAILS_PARSER_METHOD")
public class DailyStockDetailsParser {

	@Id
	@Column(name = "method")
	@Enumerated(EnumType.STRING)
	private ParserMethod parserMethod;

	public ParserMethod getParserMethod() {
		return parserMethod;
	}

	public void setParserMethod(ParserMethod parserMethod) {
		this.parserMethod = parserMethod;
	}
}
