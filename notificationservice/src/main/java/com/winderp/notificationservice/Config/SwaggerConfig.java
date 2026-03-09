package com.winderp.notificationservice.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WindERP Notification Service API")
                        .description("Microservice de gestion des notifications pour les candidatures et entretiens")
                        .version("1.0"));
    }
}