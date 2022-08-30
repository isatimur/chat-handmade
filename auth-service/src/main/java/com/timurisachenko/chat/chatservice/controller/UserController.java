package com.timurisachenko.chat.chatservice.controller;

import com.timurisachenko.chat.chatservice.exception.ResourceNotFoundException;
import com.timurisachenko.chat.chatservice.model.ChatUserDetails;
import com.timurisachenko.chat.chatservice.model.User;
import com.timurisachenko.chat.chatservice.payload.ApiResponse;
import com.timurisachenko.chat.chatservice.payload.UserSummary;
import com.timurisachenko.chat.chatservice.service.UserService;
import com.timurisachenko.chat.chatservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api")
public class UserController {
    private static final long DELAY_PER_ITEM_MS = 100l;
    private UserService userService;

    @Autowired
    public UserController(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/me/picture")
    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<ApiResponse>> updateProfilePicture(
            @RequestBody String profilePicture,
            @AuthenticationPrincipal ChatUserDetails userDetails) {

        return userService.updateProfilePicture(profilePicture, userDetails.getId())
                .map((user) -> ok()
                        .body(new ApiResponse(true, "Profile picture updated successfully")));
    }

    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> findUser(@PathVariable("username") String username) {
        log.info("retrieving user {}", username);

        return userService.findByUsername(username).map(user -> ok(user)).doOnError((error) -> new ResourceNotFoundException(username));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ResponseEntity<User>> findAll() {
        log.info("retrieving all users");

        return userService.findAll().map(el -> ok(el)).delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('FACEBOOK_USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserSummary> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return SecurityUtils.getCurrentUserLogin().flatMap(
                        username -> userService.findByUsername(username))
                .map(user -> UserSummary.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getUserProfile().getDisplayName())
                        .profilePicture(user
                                .getUserProfile()
                                .getProfilePictureUrl())
                        .build());
    }

    @GetMapping(value = "/users/summary/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UserSummary>> getUserSummary(@PathVariable("username") String username) {
        log.info("retrieving user {}", username);

        return userService.findByUsername(username)
                .map(user -> ok(convertTo(user)))
                .doOnError((error) -> new ResourceNotFoundException(username));
    }

    @PostMapping(value = "/users/summary/in", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ResponseEntity<UserSummary>> getUserSummaries(@RequestBody List<String> usernames) {
        log.info("retrieving summaries for {} usernames", usernames.size());

        return userService
                .findByUsernameIn(usernames).buffer()
                .flatMapIterable(userList -> userList)
                .doOnNext(user -> convertTo((User) user))
                .cast(UserSummary.class)
                .map(summary -> ok(summary));


    }

    @GetMapping(value = "/users/summaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserSummary> findAllUserSummaries() {
        log.info("retrieving all users summaries");
        return SecurityUtils.getCurrentUserLogin()
                .flatMapMany(un -> userService.findAll()
                        .filter(user -> !user.getUsername().equals(un))
                        .map(u -> Tuples.of(u, un))
                        .map(tuple -> tuple.getT1())
                        .map(this::convertTo));
    }

    private UserSummary convertTo(User user) {
        return UserSummary
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile() != null ? user.getUserProfile().getDisplayName() : null)
                .profilePicture(user.getUserProfile() != null ? user.getUserProfile().getProfilePictureUrl() : null)
                .build();
    }
}
