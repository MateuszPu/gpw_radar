package com.gpw.radar.service.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;

@Service
public class WebParserService {

	private final Logger logger = LoggerFactory.getLogger(WebParserService.class);

	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;

	private final DateTimeFormatter dtfTypeOne = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final DateTimeFormatter dtfTypeTwo = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter localTimeTyoe = DateTimeFormatter.ofPattern("HH:mm:ss");
	private LocalDate date;
    private LocalTime time;

	public LocalDate parseLocalDateFromString(String date) {
		if (date.contains("-")) {
			this.date = LocalDate.parse(date, dtfTypeOne);
		} else {
			this.date = LocalDate.parse(date, dtfTypeTwo);
		}
		return this.date;
	}

    public LocalTime parseLocalTimeFromString(String time) {
        this.time = LocalTime.parse(time, localTimeTyoe);
        return this.time;
    }

	public Stock setNameAndShortName(Stock stock) {
		Document doc = null;
		try {
			doc = getDocumentFromStooqWeb(stock.getTicker().toString());
		} catch (IOException e) {
			logger.error("Error ocurs: " + e.getMessage());
		}

		String stockName = getStockNameFromWeb(doc);
		stock.setStockName(stockName);

		String stockShortName = getStockShortNameFromWeb(doc);
		stock.setStockShortName(stockShortName);

		return stock;
	}

	private String getStockNameFromWeb(Document doc) {
		String title = doc.title();
		String stockName = title.substring(title.indexOf("- ") + 2, title.indexOf("- Stooq"));
		String stockNameOutOfSpacesAndUpperCase = stockName.trim().toUpperCase();
		return stockNameOutOfSpacesAndUpperCase;
	}

	private String getStockShortNameFromWeb(Document doc) {
		Elements links = doc.select("meta");
		Element table = links.get(1);
		String attr = table.attr("content");
		String stockShortName = attr.substring(0, attr.indexOf(","));
		return stockShortName;
	}

	private Document getDocumentFromStooqWeb(String ticker) throws IOException {
		Document doc = Jsoup.connect("http://stooq.pl/q/?s=" + ticker).get();
		return doc;
	}

	@Transactional
	public void getStockFinanceEvent(Stock stock) {
		Document doc = null;
		try {
			doc = getDocumentFromStockWatchWeb(stock.getStockShortName());
		} catch (IOException e) {
			logger.error("Error ocurs: " + e.getMessage());
		}
		Elements elements = doc.select("div[class=CASld roundAll]");
		// if elements.size == 3 means that stock has any finance event
		// otherwise there is no finance event for stock
		if (elements.size() == 3) {
			Elements table = doc.select("table[class=cctabdt]");
			int indexOfCorrectTable = table.size() - 2;
			Element correctTable = table.get(indexOfCorrectTable);
			Elements tr = correctTable.select("tr");
			for (int i = 0; i < tr.size(); i++) {
				stockFinanceEventRepository.save(prepareStockFinanceEvent(stock, tr, i));
			}
		}
	}

	public LocalDate getLastDateWig20FromStooqWebsite() {
		String line = "";
		String cvsSplitBy = ",";
		LocalDate date = null;
		try {
			URL urlStooq = new URL("http://stooq.pl/q/l/?s=wig20&f=sd2t2ohlcv&h&e=csv");
			URLConnection stooqConnection = urlStooq.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(stooqConnection.getInputStream()));
			// skip first line as there are a headers
			in.readLine();
			line = in.readLine();
			String[] stockDetailsFromCsv = line.split(cvsSplitBy);
			date = parseLocalDateFromString(stockDetailsFromCsv[1]);
		} catch (MalformedURLException e) {
			logger.error("Error ocurs: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Error ocurs: " + e.getMessage());
		}
		return date;
	}

	private StockFinanceEvent prepareStockFinanceEvent(Stock stock, Elements tr, int i) {
		final int positionOfDate = 0;
		final int positionOfMessage = 1;
		Elements select = tr.get(i).select("td");
		LocalDate date = parseLocalDateFromString(select.get(positionOfDate).text());
		String message = select.get(positionOfMessage).text();

		StockFinanceEvent stockFinanceEvent = new StockFinanceEvent();
		stockFinanceEvent.setStock(stock);
		stockFinanceEvent.setDate(date);
		stockFinanceEvent.setMessage(message);
		return stockFinanceEvent;
	}

	private Document getDocumentFromStockWatchWeb(String stockShortName) throws IOException {
		String url = "http://www.stockwatch.pl/gpw/" + stockShortName + ",wykres-swiece,wskazniki.aspx";
		Document doc = Jsoup.connect(url).get();
		return doc;
	}
}
