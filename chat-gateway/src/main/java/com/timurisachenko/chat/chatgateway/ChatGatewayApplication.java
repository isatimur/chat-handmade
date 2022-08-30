package com.timurisachenko.chat.chatgateway;

//import com.timurisachenko.chat.chatgateway.web.filter.AuthenticationFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.DispatcherHandler;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableEurekaClient
@ConditionalOnClass({ DispatcherHandler.class,
        ReactiveResilience4JAutoConfiguration.class })
@OpenAPIDefinition(info = @Info(title = "APIs", version = "1.0", description = "Documentation APIs v1.0"))
public class ChatGatewayApplication {

//    final AuthenticationFilter filter;

//    public ChatGatewayApplication(
//            AuthenticationFilter filter) {
//        this.filter = filter;
//    }

    public static void main(String[] args) {
        SpringApplication.run(ChatGatewayApplication.class, args);
        Hooks.onOperatorDebug();
    }
//
//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("auth-service", r -> r.path("/auth/**")
//                        .filters(f -> f.filter(filter))
//                        .uri("lb://auth-service"))
//                .build();
//
//    }
}
