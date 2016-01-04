package com.gpw.radar.domain.database;

import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

import javax.persistence.*;

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
