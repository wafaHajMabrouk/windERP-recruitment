package com.winderp.notificationservice.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long candidatureId;
    private Long interviewId;

    @Column(nullable = false)
    private String status; // ex: "SENT"

    @Column(nullable = false)
    private Boolean readed = false; // ⚠ important : DEFAULT false

    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    private String message; // texte de la notification
}