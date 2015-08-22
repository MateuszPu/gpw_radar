package com.gpw.radar.service;

import java.io.IOException;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockFinanceEvent;
import com.gpw.radar.repository.StockFinanceEventRepository;


@Service
@Transactional
public class WebParserService {

	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;

	private final Logger log = LoggerFactory.getLogger(WebParserService.class);
	final DateTimeFormatter dtfTypeOne = DateTimeFormat.forPattern("yyyy-MM-dd");
	final DateTimeFormatter dtfTypeTwo = DateTimeFormat.forPattern("yyyyMMdd");
	private LocalDate dt;

	public LocalDate parseLocalDateFromString(String date) {
		if (date.contains("-")) {
			dt = dtfTypeOne.parseLocalDate(date);
		} else {
			dt = dtfTypeTwo.parseLocalDate(date);
		}
		return dt;
	}

	public String getNameOfStock(String ticker) throws IOException {
		Document doc = getDocumentFromStooq(ticker);
		String title = doc.title();
		String nameOfStock = title.substring(title.indexOf("- ") + 2, title.indexOf("- Stooq"));
		return nameOfStock;
	}

	public String getShortNameOfStock(String ticker) throws IOException {
		Document doc = getDocumentFromStooq(ticker);
		Elements links = doc.select("meta");
		Element table = links.get(1);
		String attr = table.attr("content");
		String shortNameOfStock = attr.substring(0, attr.indexOf(","));
		return shortNameOfStock;
	}

	private Document getDocumentFromStooq(String ticker) throws IOException {
		Document doc = Jsoup.connect("http://stooq.pl/q/?s=" + ticker).get();
		return doc;
	}

	public void getStockFinanceEvent(Stock stock) throws IOException {
		String stockShortName = stock.getStockShortName();
		String url = "http://www.stockwatch.pl/gpw/" + stockShortName + ",wykres-swiece,wskazniki.aspx";
		Document doc = Jsoup.connect(url).get();
		Elements test = doc.select("div[class=CASld roundAll]");
		if (test.size() == 3) {
		Elements table = doc.select("table[class=cctabdt]");
		int indexOfCorrectTable = table.size() - 2;
		Element correctTable = table.get(indexOfCorrectTable);
		Elements tr = correctTable.select("tr");

			for (int i = 0; i < tr.size(); i++) {
				Elements select = tr.get(i).select("td");
				LocalDate date = parseLocalDateFromString(select.get(0).text());
				String message = select.get(1).text();

				StockFinanceEvent stockFinanceEvent = new StockFinanceEvent();
				stockFinanceEvent.setStock(stock);
				stockFinanceEvent.setDate(date);
				stockFinanceEvent.setMessage(message);
				stockFinanceEventRepository.save(stockFinanceEvent);
			}
		}
	}
}