package com.winderp.dashbordservice.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/candidates/count")
    int getTotalCandidates();

}
