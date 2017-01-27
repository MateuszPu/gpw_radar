package com.gpw.radar.rabbitmq.consumer.rss.news.chat;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.MessageTransformer;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.sockets.SocketMessageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("rssChatConsumer")
@Profile("!" + Constants.SPRING_PROFILE_DEVELOPMENT)
public class Consumer {

    private final ChatMessageRepository chatMessageRepository;
    private final SocketMessageService socketMessageService;
    private final MessageTransformer messageTransformer;

    @Autowired
    public Consumer(ChatMessageRepository chatMessageRepository, SocketMessageService socketMessageService,
                    MessageTransformer messageTransformer) {
        this.chatMessageRepository = chatMessageRepository;
        this.socketMessageService = socketMessageService;
        this.messageTransformer = messageTransformer;
    }

    @RabbitListener(queues = "${rss_reader_chat_queue}")
    public void consumeMessage(Message message) throws InterruptedException, IOException {
        List<ChatMessage> chatMessages = messageTransformer.transformMessage(message);
        List<ChatMessage> save = chatMessageRepository.save(chatMessages);
        sendMessagesToChat(save);
    }

    private void sendMessagesToChat(List<ChatMessage> chatMessages) {
        chatMessages.stream()
            .sorted((e1, e2) -> e1.getCreatedDate().compareTo(e2.getCreatedDate()))
            .forEach(socketMessageService::sendToChat);
    }
}
