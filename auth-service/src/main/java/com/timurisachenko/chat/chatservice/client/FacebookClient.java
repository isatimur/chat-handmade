package com.timurisachenko.chat.chatservice.client;

import com.timurisachenko.chat.chatservice.model.facebook.FacebookUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacebookClient {

    private final WebClient webClient;

    private final String FACEBOOK_GRAPH_API_BASE = "https://graph.facebook.com";

    @Autowired
    public FacebookClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(FACEBOOK_GRAPH_API_BASE).build();
    }

    public Mono<FacebookUser> getUser(String accessToken) {
        var path = "/me?fields={fields}&redirect={redirect}&access_token={access_token}";
        var fields = "email,first_name,last_name,id,picture.width(720).height(720)";
        final Map<String, String> variables = new HashMap<>();
        variables.put("fields", fields);
        variables.put("redirect", "false");
        variables.put("access_token", accessToken);
        return webClient.get().uri(path, variables).retrieve().bodyToMono(FacebookUser.class);
    }
}
