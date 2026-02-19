package com.winderp.interviewservice.Config;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi interviewApi() {
        return GroupedOpenApi.builder()
                .group("interview-api")
                .pathsToMatch("/interviews/**")
                .build();
    }
}
