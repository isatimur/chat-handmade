package com.timurisachenko.chat.chatservice.repository;

import com.timurisachenko.chat.chatservice.model.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatHistoryRepository extends ReactiveMongoRepository<ChatRoom, String> {
}
