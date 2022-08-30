//package com.timurisachenko.chat.chatgateway.web.filter;
//
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.function.Predicate;
//
//@Component
//public class RouterValidator {
//
//    public static final List<String> openApiEndpoints = List.of(
//            "/api/users"
//            ,"/api/signin"
//            ,"/api/facebook/signin"
//            ,"/actuator/**"
//    );
//
//    public Predicate<ServerHttpRequest> isSecured =
//            request -> openApiEndpoints
//                    .stream()
//                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
//
//}
