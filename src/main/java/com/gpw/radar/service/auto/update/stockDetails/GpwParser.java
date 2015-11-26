package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//getting data from http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all
@Component("gpwParser")
public class GpwParser implements StockDetailsParser {

    @Inject
	private StockRepository stockRepository;

	private static final int indexOfTicker = 3;
	private static final int indexOfOpenPrice = 8;
	private static final int indexOfMaxPrice = 9;
	private static final int indexOfMinPrice = 10;
	private static final int indexOfClosePrice = 11;
	// private final int indexOfTransactionCount = 20;
	private static final int indexOfVolume = 21;
	private static final int indexOfLastClosePrice = 6;
	private static final DateTimeFormatter dtfType = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
				std.setOpenPrice(new BigDecimal(getElement(select, indexOfOpenPrice)));
				std.setMaxPrice(new BigDecimal(getElement(select, indexOfMaxPrice)));
				std.setMinPrice(new BigDecimal(getElement(select, indexOfMinPrice)));
				std.setClosePrice(new BigDecimal(getElement(select, indexOfClosePrice)));
				std.setVolume(Long.valueOf(getElement(select, indexOfVolume)));
			} catch (NumberFormatException ex) {
				// if string is not a valid presentation of number that means
				// the stockdetails was not qouted
				std.setOpenPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
				std.setMaxPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
				std.setMinPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
				std.setClosePrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
				std.setVolume(0l);
			}
			stockDetails.add(std);
		}
		return stockDetails;
	}

	private String getElement(Elements select, int indexOfElement) {
		return select.get(indexOfElement).text().replace(",", ".").replace("\u00a0", "");
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
		LocalDate date = LocalDate.parse(el.first().text(), dtfType);
		return date;
	}

	@Override
	public void setQutesDate(LocalDate quotesDate) {
	}
}
