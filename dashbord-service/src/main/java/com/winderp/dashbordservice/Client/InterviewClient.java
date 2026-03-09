package com.winderp.dashbordservice.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "interview-service")
public interface InterviewClient {

    @GetMapping("/api/interviews/count")
    int getTotalInterviews();

}