package com.gpw.radar.service.database.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateAndTimeParserService {

	private final Logger logger = LoggerFactory.getLogger(DateAndTimeParserService.class);

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

}
