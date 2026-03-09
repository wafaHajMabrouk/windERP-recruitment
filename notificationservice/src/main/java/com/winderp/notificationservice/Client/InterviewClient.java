package com.winderp.notificationservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "interview-service")
public interface InterviewClient {

    @GetMapping("/api/interviews/{id}")
    ResponseEntity<Object> getInterviewById(@PathVariable("id") Long id);
}