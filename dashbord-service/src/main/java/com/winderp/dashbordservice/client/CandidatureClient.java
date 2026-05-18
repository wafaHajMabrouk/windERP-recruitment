package com.winderp.dashbordservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "candidate-service")
public interface CandidatureClient {
    @GetMapping("/api/candidatures")
    List<Object> getAllCandidatures();


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