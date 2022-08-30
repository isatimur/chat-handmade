package com.timurisachenko.chat.chatservice;

import com.timurisachenko.chat.chatservice.messaging.UserEventStream;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongock
@EnableBinding(UserEventStream.class)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        Hooks.onOperatorDebug();
    }

}
