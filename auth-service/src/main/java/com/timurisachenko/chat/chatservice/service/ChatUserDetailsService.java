package com.timurisachenko.chat.chatservice.service;

import com.timurisachenko.chat.chatservice.exception.UserNotActivatedException;
import com.timurisachenko.chat.chatservice.model.ChatUserDetails;
import com.timurisachenko.chat.chatservice.model.User;
import com.timurisachenko.chat.chatservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class ChatUserDetailsService implements ReactiveUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(ChatUserDetailsService.class);

    private final UserRepository userRepository;

    @Autowired
    public ChatUserDetailsService(final @Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);

        return userRepository
                .findByUsername(lowercaseLogin)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database")))
                .map(ChatUserDetails::new)
                .cast(UserDetails.class)
                .doOnError((error) -> new UsernameNotFoundException("Username not found")).log();

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.isActive()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
