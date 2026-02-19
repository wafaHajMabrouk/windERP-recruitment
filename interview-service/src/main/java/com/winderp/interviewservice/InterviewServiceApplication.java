package com.winderp.interviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.winderp.interviewservice.Client")
public class InterviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterviewServiceApplication.class, args);
    }
}
