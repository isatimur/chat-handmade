package com.timurisachenko.chat.chatservice.controller;

import com.timurisachenko.chat.chatservice.exception.BadRequestException;
import com.timurisachenko.chat.chatservice.model.Profile;
import com.timurisachenko.chat.chatservice.model.Role;
import com.timurisachenko.chat.chatservice.model.User;
import com.timurisachenko.chat.chatservice.payload.ApiResponse;
import com.timurisachenko.chat.chatservice.payload.FacebookLoginRequest;
import com.timurisachenko.chat.chatservice.payload.JwtAuthenticationResponse;
import com.timurisachenko.chat.chatservice.payload.LoginRequest;
import com.timurisachenko.chat.chatservice.payload.SignUpRequest;
import com.timurisachenko.chat.chatservice.service.FacebookService;
import com.timurisachenko.chat.chatservice.service.TokenProvider;
import com.timurisachenko.chat.chatservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/api")
public class AuthController {
    private final UserService userService;
    private final FacebookService facebookService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Autowired
    public AuthController(@Qualifier("userService") UserService userService, FacebookService facebookService, ReactiveAuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.facebookService = facebookService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/facebook/signin")
    @Operation(summary = "Facebook sign in")
    public  Mono<ResponseEntity> facebookAuth(@Valid @RequestBody Mono<FacebookLoginRequest> facebookLoginRequest) {
        return facebookLoginRequest.flatMap(fbLogin -> facebookService.loginUser(fbLogin.getAccessToken()))
                .map(jwt ->ResponseEntity.ok(new JwtAuthenticationResponse(jwt)));
    }

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Custom sign in")
    public Mono<ResponseEntity> authenticateUser(@Valid @RequestBody Mono<LoginRequest> loginRequest) {

        return loginRequest
                .flatMap(login -> authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
                                )
                                .flatMap(auth -> Mono.fromCallable(() -> tokenProvider.createToken(auth, false)))
                ).map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("accessToken", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                });
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register user")
    public Mono<ResponseEntity<ApiResponse>> createUser(@Valid @RequestBody Mono<SignUpRequest> signUpRequest) {
//        log.info("creating user {}", payload.getUsername());
        return signUpRequest
                .flatMap(request -> {
                    User user = User
                            .builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(request.getPassword())
                            .userProfile(Profile
                                    .builder()
                                    .displayName(request.getName())
                                    .profilePictureUrl(request.getProfilePicUrl())
                                    .build())
                            .build();
                    return userService.registerUser(user, Role.USER);
                })
                .map(user -> {
                    var location = UriComponentsBuilder.fromPath("/users/{username}").buildAndExpand(user.getUsername()).toUri();
                    return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
                }).doOnError(error -> {
                    throw new BadRequestException(error.getMessage());
                });

    }

}
