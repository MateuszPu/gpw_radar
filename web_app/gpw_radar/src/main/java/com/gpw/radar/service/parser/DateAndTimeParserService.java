package com.gpw.radar.service.parser;

import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DateAndTimeParserService {

    private final Logger logger = LoggerFactory.getLogger(DateAndTimeParserService.class);

    private final UrlStreamsGetterService urlStreamsGetterService;

    @Resource
    private Map<String, DateTimeFormatter> localDateTimeFormatter;

    @Resource
    private Map<String, DateTimeFormatter> localTimeFormatter;

    @Autowired
    public DateAndTimeParserService(final UrlStreamsGetterService urlStreamsGetterService) {
        this.urlStreamsGetterService = urlStreamsGetterService;
    }

    public DateAndTimeParserService(final UrlStreamsGetterService urlStreamsGetterService,
                                    Map<String, DateTimeFormatter> localDateTimeFormatter,
                                    Map<String, DateTimeFormatter> localTimeFormatter) {
        this.urlStreamsGetterService = urlStreamsGetterService;
        this.localDateTimeFormatter = localDateTimeFormatter;
        this.localTimeFormatter = localTimeFormatter;
    }

    public LocalDate parseLocalDateFromString(String date) {
        String key = localDateTimeFormatter.keySet().stream().filter(k -> date.matches(k)).findFirst().get();
        return LocalDate.parse(date, localDateTimeFormatter.get(key));
    }

    public LocalTime parseLocalTimeFromString(String time) {
        String key = localTimeFormatter.keySet().stream().filter(k -> time.matches(k)).findFirst().get();
        return LocalTime.parse(time, localTimeFormatter.get(key));
    }

    public LocalTime getFiveMinutesTime(LocalTime timeToProcess) {
        LocalTime time = timeToProcess;
        if (time.isAfter(LocalTime.of(16, 50))) {
            time = LocalTime.of(16, 50);
        } else {
            time = LocalTime.of(time.getHour(), (time.getMinute() / 5) * 5);
        }
        return time;
    }

    public LocalDate getLastDateWig20FromStooqWebsite() {
        String line = "";
        String cvsSplitBy = ",";
        LocalDate date = null;
        String url = "http://stooq.pl/q/l/?s=wig20&f=sd2t2ohlcv&h&e=csv";
        try (BufferedReader bufferedReader = new BufferedReader(urlStreamsGetterService.getInputStreamReaderFromUrl(url))) {
            // skip first line as there are a headers
            bufferedReader.readLine();
            line = bufferedReader.readLine();
            String[] stockDetailsFromCsv = line.split(cvsSplitBy);
            date = parseLocalDateFromString(stockDetailsFromCsv[1]);
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return date;
    }

    public String getStringFromDate(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(dateTimeFormatter);
    }
}
