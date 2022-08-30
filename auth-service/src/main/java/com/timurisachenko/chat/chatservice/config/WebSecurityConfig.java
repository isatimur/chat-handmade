package com.timurisachenko.chat.chatservice.config;


import com.timurisachenko.chat.chatservice.model.Role;
import com.timurisachenko.chat.chatservice.service.JWTFilter;
import com.timurisachenko.chat.chatservice.service.TokenProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport;
import tech.jhipster.config.JHipsterProperties;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Import(SecurityProblemSupport.class)
public class WebSecurityConfig {

    private final JHipsterProperties jHipsterProperties;
    @Qualifier("userDetailsService")
    private final ReactiveUserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final SecurityProblemSupport problemSupport;

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService
        );
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        http
                .securityMatcher(new NegatedServerWebExchangeMatcher(new OrServerWebExchangeMatcher(
                        pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**", "/v3/api-docs/**", "/test/**", "/api/users", "/api/signin", "/api/facebook/signin"),
                        pathMatchers(HttpMethod.OPTIONS, "/**")
                )))
                .csrf()
                .disable()
                .addFilterAt(new JWTFilter(tokenProvider, userDetailsService), SecurityWebFiltersOrder.HTTP_BASIC)
                .authenticationManager(reactiveAuthenticationManager())
                .exceptionHandling()
                .accessDeniedHandler(problemSupport)
                .authenticationEntryPoint(problemSupport)
                .and()
                .headers()
                .contentSecurityPolicy(jHipsterProperties.getSecurity().getContentSecurityPolicy())
                .and()
                .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
                .and()
                .frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY)
                .and()
                .requestCache()
                .requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .authorizeExchange()
                .pathMatchers("/api/authenticate").permitAll()
                .pathMatchers("/api/users").permitAll()
                .pathMatchers("/api/signin").permitAll()
                .pathMatchers("/api/facebook/signin").permitAll()
                .pathMatchers("/api/auth-info").permitAll()
                .pathMatchers("/api/admin/**").hasAuthority("ROLE_" + Role.ADMIN.getName())
                .pathMatchers("/api/**").authenticated()
                .pathMatchers("/management/health").permitAll()
                .pathMatchers("/management/health/**").permitAll()
                .pathMatchers("/management/info").permitAll()
                .pathMatchers("/management/prometheus").permitAll()
                .pathMatchers("/management/**").hasAuthority("ROLE_" + Role.ADMIN.getName());
        // @formatter:on
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ControllerAdvice
    public static class SecurityExceptionHandling implements ProblemHandling, SecurityAdviceTrait {
    }

    @Bean
    public AdviceTrait securityExceptionHandling() {
        return new SecurityExceptionHandling();
    }

}
