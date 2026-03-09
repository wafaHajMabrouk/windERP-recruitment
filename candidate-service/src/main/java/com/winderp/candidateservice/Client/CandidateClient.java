package com.winderp.candidateservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "auth-service") // Nom du microservice auth-service
public interface CandidateClient {

    @GetMapping("/api/users/{id}") // L'endpoint exact de ton auth-service
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}