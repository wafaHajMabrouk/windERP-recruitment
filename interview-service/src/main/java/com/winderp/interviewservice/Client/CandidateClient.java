package com.winderp.interviewservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "candidate-service",
        url = "http://localhost:8089",  // ← URL explicite (sans load balancer)
        contextId = "candidateClient"
)
public interface CandidateClient {

    @GetMapping("/api/candidatures/exists/{id}")
    Boolean existsById(@PathVariable Long id);

    @GetMapping("/api/candidatures/{id}/candidateId")
    Long getCandidatIdByCandidatureId(@PathVariable Long id);

    @GetMapping("/api/candidatures/{id}/candidateName")
    String getCandidateNameByCandidatureId(@PathVariable("id") Long id);

    @GetMapping("/api/candidatures/{id}/isAccepted")
    Boolean isCandidatureAccepted(@PathVariable Long id);
}