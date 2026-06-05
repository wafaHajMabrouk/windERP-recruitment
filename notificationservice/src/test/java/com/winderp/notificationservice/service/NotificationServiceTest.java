package com.winderp.notificationservice.service;

import com.winderp.notificationservice.models.Notification;
import com.winderp.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 1, 15, 12, 0);

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification1;
    private Notification notification2;
    private List<Notification> notificationList;

    @BeforeEach
    void setUp() {
        notification1 = Notification.builder()
                .id(1L)
                .userId(100L)
                .candidatureId(1000L)
                .status("SENT")
                .readed(false)
                .dateEnvoi(FIXED_DATE.minusDays(1))
                .message("Votre candidature a été acceptée")
                .type("SUCCESS")
                .build();

        notification2 = Notification.builder()
                .id(2L)
                .userId(100L)
                .status("SENT")
                .readed(true)
                .dateEnvoi(FIXED_DATE.minusHours(5))
                .message("Entretien programmé demain à 14h")
                .type("INFO")
                .build();

        notificationList = Arrays.asList(notification1, notification2);
    }

    @Test
    @DisplayName("saveNotification - Créer et sauvegarder une notification")
    void testSaveNotification_Success() {
        Notification newNotification = Notification.builder()
                .userId(100L)
                .candidatureId(1000L)
                .message("Test notification")
                .type("INFO")
                .build();

        Notification savedNotification = Notification.builder()
                .id(3L)
                .userId(100L)
                .candidatureId(1000L)
                .status("SENT")
                .readed(false)
                .dateEnvoi(FIXED_DATE)
                .message("Test notification")
                .type("INFO")
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.saveNotification(newNotification);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(100L, result.getUserId());
        assertFalse(result.getReaded());
        assertNotNull(result.getDateEnvoi());

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/100"), any(Notification.class));
    }

    @Test
    @DisplayName("notifyUser - Notification automatique")
    void testNotifyUser_Success() {
        Notification savedNotification = Notification.builder()
                .id(4L)
                .userId(200L)
                .message("Nouvelle notification")
                .readed(false)
                .dateEnvoi(FIXED_DATE)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notificationService.notifyUser(200L, "Nouvelle notification");

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/200"), any(Notification.class));
    }

    @Test
    @DisplayName("getUserNotifications - Récupérer notifications d'un user")
    void testGetUserNotifications_Success() {
        when(notificationRepository.findByUserIdOrderByDateEnvoiDesc(100L)).thenReturn(notificationList);

        List<Notification> result = notificationService.getUserNotifications(100L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Votre candidature a été acceptée", result.get(0).getMessage());

        verify(notificationRepository, times(1)).findByUserIdOrderByDateEnvoiDesc(100L);
    }

    @Test
    @DisplayName("getUserNotifications - Aucune notification")
    void testGetUserNotifications_Empty() {
        when(notificationRepository.findByUserIdOrderByDateEnvoiDesc(300L)).thenReturn(Arrays.asList());

        List<Notification> result = notificationService.getUserNotifications(300L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(notificationRepository, times(1)).findByUserIdOrderByDateEnvoiDesc(300L);
    }

    @Test
    @DisplayName("markAsRead - Marquer une notification comme lue")
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        notificationService.markAsRead(1L);

        assertTrue(notification1.getReaded());

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(notification1);
    }

    @Test
    @DisplayName("markAsRead - Notification non trouvée")
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            notificationService.markAsRead(99L);
        });

        verify(notificationRepository, times(1)).findById(99L);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("markAllAsRead - Marquer toutes les notifications d'un user comme lues")
    void testMarkAllAsRead_Success() {
        List<Notification> unreadNotifications = Arrays.asList(notification1);
        when(notificationRepository.findByUserIdAndReadedFalse(100L)).thenReturn(unreadNotifications);
        when(notificationRepository.saveAll(anyList())).thenReturn(unreadNotifications);

        notificationService.markAllAsRead(100L);

        assertTrue(notification1.getReaded());

        verify(notificationRepository, times(1)).findByUserIdAndReadedFalse(100L);
        verify(notificationRepository, times(1)).saveAll(unreadNotifications);
    }

    @Test
    @DisplayName("markAllAsRead - Aucune notification non lue")
    void testMarkAllAsRead_NoUnreadNotifications() {
        when(notificationRepository.findByUserIdAndReadedFalse(100L)).thenReturn(Arrays.asList());

        notificationService.markAllAsRead(100L);

        verify(notificationRepository, times(1)).findByUserIdAndReadedFalse(100L);
        verify(notificationRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("count - Compter toutes les notifications")
    void testCount() {
        when(notificationRepository.count()).thenReturn(15L);

        long result = notificationService.count();

        assertEquals(15L, result);
        verify(notificationRepository, times(1)).count();
    }
}