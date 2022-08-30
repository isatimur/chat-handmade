package com.timurisachenko.chat.chatgateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

@Configuration
@Slf4j
public class OpenApiConfiguration {

    public static final String API_FIRST_PACKAGE = "com.timurisachenko.chat.web.api";

    @Bean
    @ConditionalOnMissingBean(name = "apiFirstGroupedOpenAPI")
    public GroupedOpenApi apiFirstGroupedOpenAPI(
            @Lazy JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
            JHipsterProperties jHipsterProperties
    ) {
        JHipsterProperties.ApiDocs properties = jHipsterProperties.getApiDocs();
        return GroupedOpenApi
                .builder()
                .group("openapi")
                .addOpenApiCustomiser(jhipsterOpenApiCustomizer)
                .packagesToScan(API_FIRST_PACKAGE)
                .pathsToMatch(properties.getDefaultIncludePattern())
                .build();
    }
    @Bean
    public JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer(JHipsterProperties jHipsterProperties) {
        log.debug("Initializing JHipster OpenApi customizer");
        return new JHipsterOpenApiCustomizer(jHipsterProperties.getApiDocs());
    }
}
