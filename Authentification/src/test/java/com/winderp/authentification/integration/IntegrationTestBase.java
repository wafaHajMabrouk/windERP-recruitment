package com.winderp.authentification.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // H2 Configuration
        registry.add("spring.datasource.url", () ->
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // JPA Configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");

        // Désactiver Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("eureka.client.register-with-eureka", () -> "false");
        registry.add("eureka.client.fetch-registry", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}