package com.winderp.notificationservice.Controller;

import com.winderp.notificationservice.Models.Notification;
import com.winderp.notificationservice.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // POST : envoyer une notification
    @PostMapping
    public Notification sendNotification(@RequestBody Notification notification){
        return notificationService.sendNotification(notification);
    }

    // GET : récupérer toutes les notifications d’un utilisateur
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId){
        return notificationService.getUserNotifications(userId);
    }

    // GET : compter toutes les notifications
    @GetMapping("/count")
    public long getTotalNotifications() {
        return notificationService.count();
    }
}