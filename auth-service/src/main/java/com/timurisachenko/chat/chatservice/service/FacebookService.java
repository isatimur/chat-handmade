package com.timurisachenko.chat.chatservice.service;

import com.timurisachenko.chat.chatservice.client.FacebookClient;
import com.timurisachenko.chat.chatservice.exception.InternalServerException;
import com.timurisachenko.chat.chatservice.model.ChatUserDetails;
import com.timurisachenko.chat.chatservice.model.Profile;
import com.timurisachenko.chat.chatservice.model.User;
import com.timurisachenko.chat.chatservice.model.facebook.FacebookUser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

import static com.timurisachenko.chat.chatservice.model.Role.FACEBOOK_USER;

@Slf4j
@Service("facebookService")
public class FacebookService {

    private FacebookClient facebookClient;
    private UserService userService;
    private TokenProvider tokenProvider;

    public FacebookService(FacebookClient facebookClient, @Qualifier("userService") UserService userService, TokenProvider tokenProvider) {
        this.facebookClient = facebookClient;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    public Mono<String> loginUser(String fbAccessToken) {
        var facebookUserMono = facebookClient.getUser(fbAccessToken);

        return facebookUserMono.flatMap(facebookUser -> userService.findById(facebookUser.getId()).or(getUser(facebookUser))
                .map(ChatUserDetails::new)
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()))
                .map(tokenProvider::createToken)
                .doOnError((exc) ->
                        new InternalServerException("unable to login facebook user id " + facebookUserMono.block().getId())));
    }

    private Mono<? extends User> getUser(final FacebookUser facebookUser) {
        return userService.registerUser(convertTo(facebookUser), FACEBOOK_USER);
    }

    private User convertTo(@NonNull FacebookUser facebookUser) {
        return User.builder()
                .id(facebookUser.getId())
                .email(facebookUser.getEmail())
                .username(generateUsername(facebookUser.getFirstName(), facebookUser.getLastName()))
                .password(generatePassword(8))
                .userProfile(Profile.builder()
                        .displayName(String
                                .format("%s %s", facebookUser.getFirstName(), facebookUser.getLastName()))
                        .profilePictureUrl(facebookUser.getPicture().getData().getUrl())
                        .build())
                .build();
    }

    private String generateUsername(String firstName, String lastName) {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%s.%s.%06d", firstName, lastName, number);
    }

    private String generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }
}
