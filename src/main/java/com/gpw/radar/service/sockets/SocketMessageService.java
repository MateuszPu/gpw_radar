package com.gpw.radar.service.sockets;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

@Service
public class SocketMessageService {

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    public void sendMostActiveStocksToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        timeStockFiveMinuteDetails.setTime(time);
        timeStockFiveMinuteDetails.setListOfDetails(stockFiveMinutesDetails);
        messagingTemplate.convertAndSend("/most/active/stocks", timeStockFiveMinuteDetails);
    }
}
