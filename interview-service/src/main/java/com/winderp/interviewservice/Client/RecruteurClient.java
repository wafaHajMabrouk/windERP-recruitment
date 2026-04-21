package com.winderp.interviewservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",        // ou "user-service" selon ton Eureka
        contextId = "recruteurClient",url = "http://localhost:8087"
)
public interface RecruteurClient {

    @GetMapping("/api/recruteurs/{id}/name")   // ← Change ici si l'endpoint réel est différent
    String getRecruteurName(@PathVariable("id") Long id);

    // Optionnel : si tu veux vérifier l'existence
    @GetMapping("/api/recruteurs/exists/{id}")
    Boolean existsById(@PathVariable("id") Long id);
}