package com.gpw.radar.service.auto.update.stockDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockRepository;

//getting data from http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all
@Component("gpwParser")
public class GpwParser implements StockDetailsParser {

	@Autowired
	private StockRepository stockRepository;

	private static final int indexOfTicker = 3;
	private static final int indexOfOpenPrice = 8;
	private static final int indexOfMaxPrice = 9;
	private static final int indexOfMinPrice = 10;
	private static final int indexOfClosePrice = 11;
	// private final int indexOfTransactionCount = 20;
	private static final int indexOfVolume = 21;
	private static final int indexOfLastClosePrice = 6;
	private static final DateTimeFormatter dtfType = DateTimeFormat.forPattern("dd-MM-yyyy");

	@Override
	public List<StockDetails> getCurrentStockDetails() {
		List<StockDetails> stockDetails = new ArrayList<StockDetails>();
		Elements tableRows = getTableRowsContentFromWeb();
		StockTicker ticker;
		LocalDate date = getCurrentDateOfStockDetails();

		// start from index 2 to skip table title
		for (int index = 2; index < tableRows.size() - 1; index++) {

			// skip the table title showing every 20 stock details
			if (index % 22 == 0) {
				index++;
				continue;
			}
			Elements select = tableRows.get(index).select("td");

			try {
				ticker = StockTicker.valueOf(select.get(indexOfTicker).text().toLowerCase());
			} catch (IllegalArgumentException ex) {
				// if the ticker from web is not in database skip to next step
				continue;
			}

			StockDetails std = new StockDetails();
			Stock stock = stockRepository.findByTicker(ticker);
			std.setStock(stock);

			std.setDate(date);
			try {
				std.setOpenPrice(new BigDecimal(select.get(indexOfOpenPrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setMaxPrice(new BigDecimal(select.get(indexOfMaxPrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setMinPrice(new BigDecimal(select.get(indexOfMinPrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setClosePrice(new BigDecimal(select.get(indexOfClosePrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setVolume(Long.valueOf(select.get(indexOfVolume).text().replace("\u00a0", "")));
			} catch (NumberFormatException ex) {
				// if string is not a valid presentation of number that means
				// the stockdetails was not qouted
				std.setOpenPrice(new BigDecimal(select.get(indexOfLastClosePrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setMaxPrice(new BigDecimal(select.get(indexOfLastClosePrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setMinPrice(new BigDecimal(select.get(indexOfLastClosePrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setClosePrice(new BigDecimal(select.get(indexOfLastClosePrice).text().replace(",", ".").replace("\u00a0", "")));
				std.setVolume(0l);
			}
			stockDetails.add(std);
		}
		return stockDetails;
	}

	private Elements getTableRowsContentFromWeb() {
		String htmlContent = getHtmlContent();
		Document doc = Jsoup.parse(htmlContent);
		Elements tableRows = doc.select("tr");
		return tableRows;
	}

	private String getHtmlContent() {
		String htmlContent = "";

		try {
			BufferedReader bufferedReader = getBufferedReaderFromUrl();

			// skip first 6 lines, as the 7th line is the html content of table
			for (int i = 0; i < 7; i++) {
				bufferedReader.readLine();
			}
			htmlContent = bufferedReader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlContent;
	}

	private BufferedReader getBufferedReaderFromUrl() throws IOException {
		URLConnection urlConnection = null;
		try {
			URL url = new URL("http://www.gpw.pl/ajaxindex.php?action=GPWQuotations&start=showTable&tab=all&lang=PL&full=1");
			urlConnection = url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		return br;
	}

	private LocalDate getCurrentDateOfStockDetails() {
		Document doc = null;
		try {
			doc = Jsoup.connect("http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all").get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Elements el = doc.select("div[class=\"colFL\"]");
		LocalDate date = dtfType.parseLocalDate(el.first().text());
		return date;
	}

	@Override
	public void setQutesDate(LocalDate quotesDate) {
	}
}
