package com.gpw.radar.domain.stock;

import java.time.LocalTime;
import java.util.List;

public class TimeStockFiveMinuteDetails {

    private LocalTime time;
    private List<StockFiveMinutesDetails> listOfDetails;

    public List<StockFiveMinutesDetails> getListOfDetails() {
        return listOfDetails;
    }

    public void setListOfDetails(List<StockFiveMinutesDetails> listOfDetails) {
        this.listOfDetails = listOfDetails;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
