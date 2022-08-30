//package com.timurisachenko.chat.chatgateway.web.filter;
//
//import com.timurisachenko.chat.chatgateway.config.TokenProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@RefreshScope
//@Component
//public class AuthenticationFilter implements GatewayFilter {
//
//    private final RouterValidator routerValidator;//custom route validator
//    private final TokenProvider tokenProvider;
//
//    @Autowired
//    public AuthenticationFilter(RouterValidator routerValidator, TokenProvider tokenProvider) {
//        this.routerValidator = routerValidator;
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        if (routerValidator.isSecured.test(request)) {
//            if (this.isAuthMissing(request))
//                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
//
//            final String token = this.getAuthHeader(request);
//
//            if (!tokenProvider.validateToken(token))
//                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
//
//            this.populateRequestWithHeaders(exchange, token);
//        }
//        return chain.filter(exchange);
//    }
//
//
//    /*PRIVATE*/
//
//    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(httpStatus);
//        return response.setComplete();
//    }
//
//    private String getAuthHeader(ServerHttpRequest request) {
//        return request.getHeaders().getOrEmpty("Authorization").get(0);
//    }
//
//    private boolean isAuthMissing(ServerHttpRequest request) {
//        return !request.getHeaders().containsKey("Authorization");
//    }
//
//    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
//        var claims = tokenProvider.getAuthentication(token);
//        exchange.getRequest().mutate()
//                .header("username", String.valueOf(((UserDetails) claims.getDetails()).getUsername()))
//                .build();
//    }
//}
