package com.winderp.interviewservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        name = "CANDIDATE-SERVICE",
        contextId = "candidateClient"
)
public interface CandidateClient {

    @GetMapping("/api/candidatures/exists/{id}")
    Boolean existsById(@PathVariable Long id);
}
