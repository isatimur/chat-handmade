package com.timurisachenko.chat.chatdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ChatDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatDiscoveryApplication.class, args);
	}

}
