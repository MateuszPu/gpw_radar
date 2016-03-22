package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class StockFiveMinutesDetailsService {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    public ResponseEntity<TimeStockFiveMinuteDetails> findTodaysStockFiveMinutesDetails(Long dateTime) {
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(dateTime).atZone(ZoneId.systemDefault());
        LocalDate date = zonedDateTime.toLocalDate();
        LocalTime time = dateAndTimeParserService.getFiveMinutesTime(zonedDateTime.toLocalTime());
        return getTimeStockFiveMinuteDetailsResponseEntity(date, time);
    }

    private ResponseEntity<TimeStockFiveMinuteDetails> getTimeStockFiveMinuteDetailsResponseEntity(LocalDate date, LocalTime time) {
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        Optional<List<StockFiveMinutesDetails>> std = stockFiveMinutesDetailsRepository.findByDateAndTime(date, time);
        if (std.isPresent()) {
            timeStockFiveMinuteDetails = getDetails(std.get(), time);
        }
        return new ResponseEntity<TimeStockFiveMinuteDetails>(timeStockFiveMinuteDetails, HttpStatus.OK);
    }

    private TimeStockFiveMinuteDetails getDetails(List<StockFiveMinutesDetails> std, LocalTime time) {
        TimeStockFiveMinuteDetails result = new TimeStockFiveMinuteDetails();
        result.setTime(time);
        result.setListOfDetails(std);
        return result;
    }
}
