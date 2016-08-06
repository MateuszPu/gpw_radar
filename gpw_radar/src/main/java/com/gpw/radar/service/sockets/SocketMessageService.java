package com.gpw.radar.service.sockets;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.service.mapper.ChatMessageMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class SocketMessageService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageMapper chatMessageMapper;

    @Autowired
    public SocketMessageService(SimpMessageSendingOperations messagingTemplate, ChatMessageMapper chatMessageMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageMapper = chatMessageMapper;
    }

    public void sendMostActiveStocksToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        timeStockFiveMinuteDetails.setTime(time);
        timeStockFiveMinuteDetails.setListOfDetails(stockFiveMinutesDetails);
        messagingTemplate.convertAndSend("/most/active/stocks", timeStockFiveMinuteDetails);
    }

    public void sendToChat(ChatMessage message) {
        ChatMessageDTO chatMessageDTO = chatMessageMapper.mapToDto(message);
        messagingTemplate.convertAndSend("/webchat/recive", chatMessageDTO);
    }
}
