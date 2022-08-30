package com.timurisachenko.chat.chatservice.service;

import com.timurisachenko.chat.chatservice.model.ChatMessage;
import com.timurisachenko.chat.chatservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;


    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getFrom() + "— Вы вошли в комнату");
            chatMessage.setFrom("[уведомление]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getFrom() + "— Вы вошли в комнату");
            chatMessage.setFrom("[уведомление]");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}