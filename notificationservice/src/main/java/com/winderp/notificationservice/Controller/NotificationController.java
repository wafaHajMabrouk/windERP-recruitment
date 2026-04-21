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

    // POST : créer notification
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    // GET : récupérer toutes les notifications d’un user
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // PUT : marquer comme lue
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    // PUT : marquer toutes comme lues
    @PutMapping("/user/{userId}/read-all")
    public void markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
    }

    // GET : compter
    @GetMapping("/count")
    public long getTotalNotifications() {
        return notificationService.count();
    }
}