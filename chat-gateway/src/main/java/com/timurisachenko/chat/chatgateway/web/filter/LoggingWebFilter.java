package com.timurisachenko.chat.chatgateway.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingWebFilter implements WebFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggingWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        LOGGER.info("Request {} called with method {}", exchange.getRequest().getPath().value(), exchange.getRequest().getMethod());
        return chain.filter(exchange);
    }
}
