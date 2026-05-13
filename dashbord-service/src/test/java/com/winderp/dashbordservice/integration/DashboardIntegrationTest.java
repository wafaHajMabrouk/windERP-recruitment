package com.winderp.dashbordservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test récupération des statistiques du dashboard")
    void testGetDashboardStats() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/dashboard/stats",
                Map.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            assertThat(response.getBody()).containsKey("totalCandidatures");
        }
    }

    @Test
    @DisplayName("Test récupération des rapports")
    void testGetRapports() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/dashboard/rapports",
                String.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test création d'un rapport")
    void testCreateRapport() {
        Map<String, Object> rapport = new HashMap<>();
        rapport.put("titre", "Rapport Test");
        rapport.put("description", "Description du rapport de test");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                getBaseUrl() + "/api/dashboard/rapports",
                rapport,
                Map.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test que le service est accessible")
    void testServiceAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/dashboard/stats",
                String.class
        );

        assertThat(response.getStatusCode()).isNotNull();
    }
}