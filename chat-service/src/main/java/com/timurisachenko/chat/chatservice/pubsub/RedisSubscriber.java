package com.timurisachenko.chat.chatservice.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timurisachenko.chat.chatservice.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String publishMessage) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            messagingTemplate.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
