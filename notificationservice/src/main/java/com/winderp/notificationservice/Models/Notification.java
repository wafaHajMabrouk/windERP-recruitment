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
    private String status = "NEW"; // "SENT"

    @Column(nullable = false)
    private Boolean readed = false;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();
    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type = "INFO"; }