package com.timurisachenko.chat.chatservice.service;

import com.timurisachenko.chat.chatservice.exception.EmailAlreadyExistsException;
import com.timurisachenko.chat.chatservice.exception.ResourceNotFoundException;
import com.timurisachenko.chat.chatservice.exception.UsernameAlreadyExistsException;
import com.timurisachenko.chat.chatservice.messaging.UserEventSender;
import com.timurisachenko.chat.chatservice.model.Role;
import com.timurisachenko.chat.chatservice.model.User;
import com.timurisachenko.chat.chatservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@Component("userService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserEventSender userEventSender;

    public Flux<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    public Mono<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    public Mono<User> findById(String id) {
        log.info("retrieving user {}", id);
        return userRepository.findById(id);
    }

    public Flux<User> findByUsernameIn(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    public Mono<User> registerUser(User user, Role role) {
        log.info("registering user {}", user.getUsername());

        userRepository.existsByUsername(user.getUsername()).doOnNext((bool) -> {
                    if (bool) {
                        log.warn("username {} already exists.", user.getUsername());

                        throw new UsernameAlreadyExistsException(
                                String.format("username %s already exists", user.getUsername()));
                    }
                }
        );
        userRepository.existsByEmail(user.getEmail()).doOnNext((bool) -> {
            if (bool) {
                log.warn("email {} already exists.", user.getEmail());

                throw new EmailAlreadyExistsException(
                        String.format("email %s already exists", user.getEmail()));
            }
        });
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>() {{
            add(role);
        }});

        return userRepository
                .save(user)
                .doOnNext(userSaved -> userEventSender.sendUserCreated(userSaved));

    }

    public Mono<User> updateProfilePicture(String uri, String id) {
        log.info("update profile picture {} for user {}", uri, id);

        return userRepository
                .findById(id)
                .doOnNext(user -> {
                    String oldProfilePic = user.getUserProfile().getProfilePictureUrl();
                    user.getUserProfile().setProfilePictureUrl(uri);
                    userRepository
                            .save(user)
                            .doOnNext(userSaved -> userEventSender.sendUserUpdated(userSaved, oldProfilePic));

                }).doOnError((error) -> new ResourceNotFoundException(String.format("user id %s not found", id)));
    }

}

