package com.winderp.dashbordservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    // Mock des clients Feign pour éviter les appels externes
    @MockBean
    private com.winderp.dashbordservice.Client.AuthClient authClient;

    @MockBean
    private com.winderp.dashbordservice.Client.CandidatureClient candidatureClient;

    @MockBean
    private com.winderp.dashbordservice.Client.InterviewClient interviewClient;

    @MockBean
    private com.winderp.dashbordservice.Client.NotificationClient notificationClient;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // ✅ CORRECTION: Utiliser le schéma WIND_DASHBOARD
        String h2Url = "jdbc:h2:mem:testdb;" +
                "MODE=PostgreSQL;" +
                "DB_CLOSE_DELAY=-1;" +
                "INIT=CREATE SCHEMA IF NOT EXISTS WIND_DASHBOARD\\;" +
                "SET SCHEMA WIND_DASHBOARD";

        registry.add("spring.datasource.url", () -> h2Url);
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // JPA - Utiliser le schéma WIND_DASHBOARD
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "WIND_DASHBOARD");

        // Désactiver Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        // Désactiver Feign
        registry.add("feign.client.config.default.connectTimeout", () -> "1000");
        registry.add("feign.client.config.default.readTimeout", () -> "1000");
        registry.add("feign.circuitbreaker.enabled", () -> "false");

        // Désactiver Security
        registry.add("spring.security.enabled", () -> "false");
        registry.add("spring.security.csrf.enabled", () -> "false");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}