package com.winderp.dashbordservice.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "notificationservice")
public interface NotificationClient {

    @GetMapping("/api/notifications/count")
    int getTotalNotifications();

}
