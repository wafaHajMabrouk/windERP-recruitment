package com.winderp.notificationservice.controller;

import com.winderp.notificationservice.models.Notification;
import com.winderp.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification1;
    private Notification notification2;
    private List<Notification> notificationList;

    @BeforeEach
    void setUp() {
        // Notification 1
        notification1 = Notification.builder()
                .id(1L)
                .userId(100L)
                .candidatureId(1000L)
                .interviewId(10000L)
                .status("SENT")
                .readed(false)
                .dateEnvoi(LocalDateTime.now().minusDays(1))
                .message("Votre candidature a été acceptée")
                .type("SUCCESS")
                .build();

        // Notification 2
        notification2 = Notification.builder()
                .id(2L)
                .userId(100L)
                .candidatureId(1001L)
                .status("SENT")
                .readed(true)
                .dateEnvoi(LocalDateTime.now().minusHours(5))
                .message("Entretien programmé demain à 14h")
                .type("INFO")
                .build();

        notificationList = Arrays.asList(notification1, notification2);
    }

    @Test
    @DisplayName("POST /api/notifications - Créer une notification")
    void testCreateNotification_Success() throws Exception {
        Notification newNotification = Notification.builder()
                .userId(100L)
                .candidatureId(1000L)
                .message("Test notification")
                .type("INFO")
                .build();

        when(notificationService.saveNotification(any(Notification.class))).thenReturn(notification1);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.message").value("Votre candidature a été acceptée"));

        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId} - Récupérer notifications d'un user")
    void testGetUserNotifications_Success() throws Exception {
        when(notificationService.getUserNotifications(100L)).thenReturn(notificationList);

        mockMvc.perform(get("/api/notifications/user/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].message").value("Votre candidature a été acceptée"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].readed").value(true));

        verify(notificationService, times(1)).getUserNotifications(100L);
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId} - Aucune notification")
    void testGetUserNotifications_Empty() throws Exception {
        when(notificationService.getUserNotifications(200L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/notifications/user/200")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService, times(1)).getUserNotifications(200L);
    }

    @Test
    @DisplayName("PUT /api/notifications/{id}/read - Marquer comme lue")
    void testMarkAsRead_Success() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/1/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    @DisplayName("PUT /api/notifications/user/{userId}/read-all - Marquer toutes comme lues")
    void testMarkAllAsRead_Success() throws Exception {
        doNothing().when(notificationService).markAllAsRead(100L);

        mockMvc.perform(put("/api/notifications/user/100/read-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAllAsRead(100L);
    }

    @Test
    @DisplayName("GET /api/notifications/count - Compter les notifications")
    void testGetTotalNotifications() throws Exception {
        when(notificationService.count()).thenReturn(10L);

        mockMvc.perform(get("/api/notifications/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(notificationService, times(1)).count();
    }
}