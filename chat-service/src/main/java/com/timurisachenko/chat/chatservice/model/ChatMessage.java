package com.timurisachenko.chat.chatservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private MessageType type;
    private String roomId;
    private String from;
    private String message;
    private long userCount;
    private LocalDateTime timestamp;
    private MessageStatus status;

    public ChatMessage() {
    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String from, String message, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.from = from;
        this.message = message;
        this.userCount = userCount;
    }

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    public enum MessageStatus {
        RECEIVED, DELIVERED
    }

}