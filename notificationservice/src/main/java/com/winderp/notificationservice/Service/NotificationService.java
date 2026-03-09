package com.winderp.notificationservice.Service;

import com.winderp.notificationservice.Models.Notification;
import com.winderp.notificationservice.Repository.NotificationRepository;
import com.winderp.notificationservice.Client.CandidateClient;
import com.winderp.notificationservice.Client.InterviewClient;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final CandidateClient candidateClient;
    private final InterviewClient interviewClient;
    private final SimpMessagingTemplate messagingTemplate;

    // ================= SEND NOTIFICATION =================
    public Notification sendNotification(Notification notification) {

        // Vérifier candidature
        if(notification.getCandidatureId() != null) {
            try {
                candidateClient.getCandidatureById(notification.getCandidatureId());
            } catch (Exception e) {
                notification.setCandidatureId(null);
            }
        }

        // Vérifier utilisateur
        if(notification.getUserId() != null) {
            try {
                candidateClient.getCandidatureById(notification.getUserId());
            } catch (Exception e) {
                notification.setUserId(null);
            }
        }

        // Vérifier interview
        if(notification.getInterviewId() != null) {
            try {
                interviewClient.getInterviewById(notification.getInterviewId());
            } catch (Exception e) {
                notification.setInterviewId(null);
            }
        }

        // Champs obligatoires
        notification.setStatus("SENT");
        notification.setReaded(false);
        notification.setDateEnvoi(LocalDateTime.now());

        // Sauvegarder
        Notification saved = repository.save(notification);

        // Envoyer via WebSocket
        if(saved.getUserId() != null) {
            messagingTemplate.convertAndSend("/topic/notifications/" + saved.getUserId(), saved);
        }

        return saved;
    }

    // ================= GET USER NOTIFICATIONS =================
    public List<Notification> getUserNotifications(Long userId){
        return repository.findByUserId(userId);
    }

    // ================= COUNT =================
    public long count() {
        return repository.count();
    }
}