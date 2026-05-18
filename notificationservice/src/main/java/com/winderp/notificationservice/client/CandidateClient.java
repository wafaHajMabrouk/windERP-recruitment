package com.winderp.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "candidate-service")
public interface CandidateClient {

    @GetMapping("/api/candidatures/{id}")
    Object getCandidatureById(@PathVariable("id") Long id);
}