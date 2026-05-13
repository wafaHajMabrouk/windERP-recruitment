// candidate-service/src/test/java/com/winderp/candidateservice/integration/CandidateIntegrationTest.java
package com.winderp.candidateservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test santé du service")
    void testHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/actuator/health",
                String.class
        );

        assertThat(response.getStatusCode())
                .withFailMessage("Le service health doit répondre 200 OK")
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Test récupération des candidatures")
    void testGetCandidatures() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/candidatures",
                String.class
        );

        // Comme la table est vide, 200 OK est acceptable
        assertThat(response.getStatusCode())
                .withFailMessage("La réponse devrait être 200 OK (liste vide)")
                .isEqualTo(HttpStatus.OK);
    }
}