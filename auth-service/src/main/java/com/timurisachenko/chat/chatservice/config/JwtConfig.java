package com.timurisachenko.chat.chatservice.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Data
@NoArgsConstructor
@Component
public class JwtConfig {

    @Value("${security.jwt.uri:/auth/**}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{24*60*60}}")
    private int expiration;

    @Value("${security.jwt.secret:JwtSecretKey}")
    private String secret;

    @Value("${security.authentication.jwt.base64-secret:JwtSecretKey}")
    private String base64SecretKey;

    @Value("${security.authentication.jwt.token-validity-in-seconds:#{24*60*60}}")
    private int tokenValidityInSeconds;

    @Value("${security.authentication.jwt.token-validity-in-seconds-for-remember-me:#{24*60*60}}")
    private int tokenValidityInSecondsForRememberMe;

}
