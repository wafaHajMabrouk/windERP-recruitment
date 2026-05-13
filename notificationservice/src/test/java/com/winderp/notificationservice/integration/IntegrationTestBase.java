package com.winderp.notificationservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // H2 Database avec schéma WIND_NOTIFICATION
        String h2Url = "jdbc:h2:mem:testdb;" +
                "MODE=PostgreSQL;" +
                "DB_CLOSE_DELAY=-1;" +
                "INIT=CREATE SCHEMA IF NOT EXISTS WIND_NOTIFICATION\\;" +
                "SET SCHEMA WIND_NOTIFICATION";

        registry.add("spring.datasource.url", () -> h2Url);
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // JPA
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "WIND_NOTIFICATION");

        // Désactiver WebSocket pour les tests
        registry.add("spring.websocket.enabled", () -> "false");

        // Désactiver Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        // Désactiver Security
        registry.add("spring.security.enabled", () -> "false");
        registry.add("spring.security.csrf.enabled", () -> "false");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}