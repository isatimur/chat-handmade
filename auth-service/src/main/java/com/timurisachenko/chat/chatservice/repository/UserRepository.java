package com.timurisachenko.chat.chatservice.repository;

import com.timurisachenko.chat.chatservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component("userRepository")
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByUsername(String username);

    Flux<User> findByUsernameIn(List<String> usernames);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);
}
