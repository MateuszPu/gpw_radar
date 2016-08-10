package com.gpw.radar.service.sockets;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;

import java.time.LocalTime;
import java.util.List;

/**
 * Created by ppp on 2015-12-26.
 */
public interface SocketMessageHandler {
    void sendMostActiveStocksToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time);

    void sendToChat(NewsMessage message);

}
