package com.winderp.candidateservice.config;

import org.springdoc.core.models.GroupedOpenApi;   // ← CET IMPORT EST OBLIGATOIRE EN 2.x
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi candidateApi() {
        return GroupedOpenApi.builder()
                .group("candidate-api")
                .pathsToMatch("/api/**")
                .build();
    }
}