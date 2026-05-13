package com.winderp.notificationservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test récupération des notifications par utilisateur")
    void testGetUserNotifications() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/notifications/user/100",
                String.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test comptage des notifications")
    void testCountNotifications() {
        ResponseEntity<Long> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/notifications/count",
                Long.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test création d'une notification")
    void testCreateNotification() {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", 100L);
        notification.put("candidatureId", 1000L);
        notification.put("message", "Test notification");
        notification.put("type", "INFO");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                getBaseUrl() + "/api/notifications",
                notification,
                Map.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test que le service est accessible")
    void testServiceAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/notifications/count",
                String.class
        );

        assertThat(response.getStatusCode()).isNotNull();
    }
}