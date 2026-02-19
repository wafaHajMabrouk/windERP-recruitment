package com.winderp.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // route vers candidate-service
                .route("candidate-service", r -> r.path("/api/candidates/**")
                        .uri("lb://candidate-service"))

                // route vers interview-service
                .route("interview-service", r -> r.path("/api/interviews/**")
                        .uri("lb://interview-service"))
                .build();
    }

}
