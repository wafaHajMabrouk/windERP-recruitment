package com.winderp.interviewservice.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String NOTIFICATION_SERVICE_URL = "http://localhost:8090/api/notifications";

    public void sendNotification(Long userId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForObject(NOTIFICATION_SERVICE_URL, request, String.class);
        } catch (Exception e) {
            System.err.println("❌ Échec envoi notification : " + e.getMessage());
            // Ne pas relancer l'exception pour ne pas bloquer la création de l'interview
        }
    }
}