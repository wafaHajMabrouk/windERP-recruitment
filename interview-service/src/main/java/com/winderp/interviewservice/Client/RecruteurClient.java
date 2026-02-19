package com.winderp.interviewservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "CANDIDATE-SERVICE",
        contextId = "recruteurClient"
)// <-- nom exact du microservice Recruteur/Candidate dans Eureka
public interface RecruteurClient {

    /**
     * Vérifie si le recruteur existe dans Candidate-Service.
     * @param id l'identifiant du recruteur
     * @return true si existe, false sinon
     */
    @GetMapping("/api/recruteurs/exists/{id}")
    Boolean existsById(@PathVariable("id") Long id);
}
