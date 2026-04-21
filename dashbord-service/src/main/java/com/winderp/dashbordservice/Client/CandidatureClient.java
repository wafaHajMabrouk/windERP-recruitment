package com.winderp.dashbordservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "candidate-service")
public interface CandidatureClient {

    @GetMapping("/api/candidatures/count")
    int getTotalCandidatures();

    // NOUVEAU
    @GetMapping("/api/candidatures/count/acceptees")
    int getAcceptedCandidatures();
    @GetMapping("/api/offres/count/ouvertes")
    int getOffresOuvertesCount();

    @GetMapping("/api/offres/count/fermees")
    int getOffresFermeesCount();
}