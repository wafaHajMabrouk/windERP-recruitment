// recrute/Authentication/src/test/java/com/winderp/authentification/integration/IntegrationTestBase.java
package com.winderp.authentification.integration;

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
        // ===== H2 Configuration =====
        registry.add("spring.datasource.url", () ->
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS wind_auth\\;SET SCHEMA wind_auth");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // ===== JPA Configuration =====
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "");

        // ===== 🔥 Auto-approve pour les tests =====
        registry.add("app.auth.auto-approve", () -> "true");

        // ===== Désactiver Eureka =====
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        // ===== Désactiver Security =====
        registry.add("spring.security.enabled", () -> "false");
        registry.add("spring.security.csrf.enabled", () -> "false");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}