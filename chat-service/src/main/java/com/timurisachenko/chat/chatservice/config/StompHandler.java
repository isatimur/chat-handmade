package com.timurisachenko.chat.chatservice.config;

import com.timurisachenko.chat.chatservice.model.ChatMessage;
import com.timurisachenko.chat.chatservice.repository.ChatRoomRepository;
import com.timurisachenko.chat.chatservice.service.ChatService;
import com.timurisachenko.chat.chatservice.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            jwtTokenProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);
            chatRoomRepository.plusUserCount(roomId);
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).from(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);

            chatRoomRepository.minusUserCount(roomId);

            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).from(name).build());

            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}