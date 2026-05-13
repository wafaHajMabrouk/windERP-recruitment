package com.winderp.dashbordservice.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "interview-service")
public interface InterviewClient {
    @GetMapping("/api/interviews")
    List<Object> getAllInterviews();
    @GetMapping("/api/interviews/count")
    int getTotalInterviews();


}