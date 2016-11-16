package com.gpw.radar.rabbitmq.consumer.rss.news.chat;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.rabbitmq.consumer.rss.news.GpwNewsModel;
import com.gpw.radar.rabbitmq.MessageTransformer;
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
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class Consumer {

    private final ChatMessageRepository chatMessageRepository;
    private final SocketMessageService socketMessageService;
    private final Mapper<GpwNewsModel, ChatMessage> mapper;
    private final MessageTransformer messageTransformer;

    @Autowired
    public Consumer(ChatMessageRepository chatMessageRepository, SocketMessageService socketMessageService,
                    Mapper<GpwNewsModel, ChatMessage> mapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.socketMessageService = socketMessageService;
        this.mapper = mapper;
        this.messageTransformer = new MessageTransformer(null);
    }

    @RabbitListener(queues = "${rss_reader_chat_queue}")
    public void reciveMessage(Message message) throws InterruptedException, IOException {
        List<ChatMessage> chatMessages = getUserMessages(message);
        List<ChatMessage> save = chatMessageRepository.save(chatMessages);
        sendMessagesToChat(save);
    }

    public List<ChatMessage> getUserMessages(Message message) throws IOException {
        List<ChatMessage> chatMessages = mapper.transformFromJsonToDomainObject(message, GpwNewsModel.class, ChatMessage.class);
        chatMessages.forEach(e -> e.setMessage(messageTransformer.transformMessage(e.getLink(), e.getMessage())));
        chatMessages.forEach(e -> {
            User user = new User();
            user.setId("h6ehbr4khohjr116k23pon9vojv66c3eab45aui6pmau3acq1b");
            user.setLogin("system");
            e.setUser(user);
        });
        return chatMessages;
    }

    private void sendMessagesToChat(List<ChatMessage> chatMessages) {
        chatMessages.stream()
            .sorted((e1, e2) -> e1.getCreatedDate().compareTo(e2.getCreatedDate()))
            .forEach(e -> socketMessageService.sendToChat(e));
    }
}
