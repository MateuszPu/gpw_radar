package com.gpw.radar.service.sockets;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;

import java.time.LocalTime;
import java.util.List;

public interface SocketMessageHandler {
    void sendMostActiveStocksToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time);

    void sendToChat(NewsMessage message);

}
