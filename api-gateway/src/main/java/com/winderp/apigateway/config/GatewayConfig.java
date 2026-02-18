package com.winderp.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Exemple pour candidate-service
                .route("candidate-service", r -> r.path("/api/candidates/**")
                        .uri("lb://candidate-service"))
                .route("offre-service", r -> r.path("/api/offres/**")
                        .uri("lb://candidate-service"))
                .build();
    }
}
