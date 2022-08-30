package com.timurisachenko.chat.chatservice.controller;

import com.timurisachenko.chat.chatservice.model.ChatMessage;
import com.timurisachenko.chat.chatservice.repository.ChatRoomRepository;
import com.timurisachenko.chat.chatservice.service.ChatService;
import com.timurisachenko.chat.chatservice.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/api")
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     * websocket "/app/chat/message".
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        String nickname = jwtTokenProvider.getAuthentication(token).getName();
        message.setFrom(nickname);
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        chatService.sendChatMessage(message);
    }
}