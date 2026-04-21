package com.winderp.notificationservice.Service;

import com.winderp.notificationservice.Models.Notification;
import com.winderp.notificationservice.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ Créer et sauvegarder une notification
    public Notification saveNotification(Notification notification) {
        // Date et statut
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setReaded(false);

        // Sauvegarde dans la base
        Notification saved = repository.save(notification);

        // WebSocket (si utilisé)
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), saved);

        return saved;
    }

    // 🔹 Notification automatique depuis un autre service
    public void notifyUser(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setReaded(false);
        notification.setDateEnvoi(LocalDateTime.now());

        repository.save(notification); // ✅ sauvegarde obligatoire

        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByDateEnvoiDesc(userId);
    }

    public void markAsRead(Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setReaded(true);
        repository.save(n);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = repository.findByUserIdAndReadedFalse(userId);
        notifications.forEach(n -> n.setReaded(true));
        repository.saveAll(notifications);
    }

    public long count() {
        return repository.count();
    }
}