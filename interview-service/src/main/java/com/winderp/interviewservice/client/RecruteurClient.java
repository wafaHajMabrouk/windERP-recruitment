package com.winderp.interviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        contextId = "recruteurClient",url = "http://localhost:8087"
)
public interface RecruteurClient {

    @GetMapping("/api/recruteurs/{id}/name")
    String getRecruteurName(@PathVariable("id") Long id);


    @GetMapping("/api/recruteurs/exists/{id}")
    Boolean existsById(@PathVariable("id") Long id);
}