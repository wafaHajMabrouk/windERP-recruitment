// candidate-service/src/test/java/com/winderp/candidateservice/integration/IntegrationTestBase.java
package com.winderp.candidateservice.integration;

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
        // URL H2 avec création du schéma WIND_RECRUTEMENT
        String h2Url = "jdbc:h2:mem:testdb;" +
                "MODE=PostgreSQL;" +
                "DB_CLOSE_DELAY=-1;" +
                "INIT=CREATE SCHEMA IF NOT EXISTS WIND_RECRUTEMENT\\;" +
                "SET SCHEMA WIND_RECRUTEMENT";

        registry.add("spring.datasource.url", () -> h2Url);
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // JPA - Utiliser le schéma WIND_RECRUTEMENT
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "WIND_RECRUTEMENT");

        // Désactiver Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        // Désactiver Security
        registry.add("spring.security.enabled", () -> "false");
        registry.add("spring.security.csrf.enabled", () -> "false");

        // Désactiver Feign pour les tests
        registry.add("feign.client.config.default.connectTimeout", () -> "1000");
        registry.add("feign.client.config.default.readTimeout", () -> "1000");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}