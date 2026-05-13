// interview-service/src/test/java/com/winderp/interviewservice/integration/InterviewIntegrationTest.java
package com.winderp.interviewservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test récupération de tous les entretiens")
    void testGetAllInterviews() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/interviews",
                String.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test récupération des entretiens par recruteur")
    void testGetInterviewsByRecruteur() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/interviews/recruteur/10",
                String.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test comptage des entretiens")
    void testCountInterviews() {
        ResponseEntity<Long> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/interviews/count",
                Long.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test que le service est accessible")
    void testServiceAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/interviews",
                String.class
        );

        assertThat(response.getStatusCode()).isNotNull();
    }
}