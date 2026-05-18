
package com.winderp.candidateservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "http://localhost:8087")  // url explicite
public interface AuthClient {
    @GetMapping("/api/users/{id}/name")
    String getUserName(@PathVariable("id") Long id);
    @GetMapping("/api/users/{id}/email")  // NOUVEAU ENDPOINT
    String getUserEmail(@PathVariable("id") Long id);
}