package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockFiveMinutesDetailsService {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    public ResponseEntity<List<TimeStockFiveMinuteDetails>> findTodaysStockFiveMinutesDetails() {
        List<StockFiveMinutesDetails> std = stockFiveMinutesDetailsRepository.findByDate(LocalDate.now());
        List<TimeStockFiveMinuteDetails> st = new ArrayList<>();
        for (LocalTime time = LocalTime.of(9, 5); time.isBefore(LocalTime.of(16, 50)); time = time.plusMinutes(5)) {
            LocalTime t = time;
            TimeStockFiveMinuteDetails timeAndStock = new TimeStockFiveMinuteDetails();
            timeAndStock.setTime(time);
            timeAndStock.setListOfDetails(std.stream().filter(a -> a.getTime().equals(t)).collect(Collectors.toList()));
            st.add(timeAndStock);
        }
        return new ResponseEntity<List<TimeStockFiveMinuteDetails>>(st, HttpStatus.OK);
    }
}
