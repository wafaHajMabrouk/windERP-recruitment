package com.winderp.notificationservice.service;

import com.winderp.notificationservice.models.Notification;
import com.winderp.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    // Créer et sauvegarder une notification
    public Notification saveNotification(Notification notification) {
        // Date et statut avec fuseau UTC
        notification.setDateEnvoi(LocalDateTime.now(ZoneId.of("UTC")));
        notification.setReaded(false);
        if (notification.getStatus() == null) {
            notification.setStatus("NEW");
        }
        if (notification.getType() == null) {
            notification.setType("INFO");
        }

        // Sauvegarde dans la base
        Notification saved = repository.save(notification);

        // WebSocket (si utilisé)
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), saved);

        return saved;
    }

    // Notification automatique depuis un autre service
    public void notifyUser(Long userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .readed(false)
                .dateEnvoi(LocalDateTime.now(ZoneId.of("UTC")))
                .status("NEW")
                .type("INFO")
                .build();

        repository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByDateEnvoiDesc(userId);
    }

    public void markAsRead(Long id) {
        Notification n = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée id=" + id));
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