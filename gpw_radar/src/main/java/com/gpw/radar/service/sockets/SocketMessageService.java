package com.gpw.radar.service.sockets;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.service.chat.RssObserver;
import com.gpw.radar.service.rss.RssObservable;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

@Service
public class SocketMessageService implements RssObserver, SocketMessageHandler {

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    @Inject
    private RssObservable rssParserService;

    @PostConstruct
    private void init() {
        rssParserService.addRssObserver(this);
    }

    public void sendMostActiveStocksToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        timeStockFiveMinuteDetails.setTime(time);
        timeStockFiveMinuteDetails.setListOfDetails(stockFiveMinutesDetails);
        messagingTemplate.convertAndSend("/most/active/stocks", timeStockFiveMinuteDetails);
    }

    public void sendToChat(NewsMessage message) {
        ModelMapper modelMapper = new ModelMapper();
        ChatMessageDTO chatMessageDTO = modelMapper.map((ChatMessage) message, ChatMessageDTO.class);

        messagingTemplate.convertAndSend("/webchat/recive", chatMessageDTO);
    }

    @Override
    public void updateRssNewsMessage(List<NewsMessage> parsedRssNewsMessage) {
        parsedRssNewsMessage.stream()
            .sorted((e1, e2) -> e1.getCreatedDate().compareTo(e2.getCreatedDate()))
            .forEach(e -> sendToChat(e));
    }
}
