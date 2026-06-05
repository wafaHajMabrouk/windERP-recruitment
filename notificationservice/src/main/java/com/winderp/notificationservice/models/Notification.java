package com.winderp.notificationservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private String status;

    @Column(nullable = false)
    private Boolean readed;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "NEW";
        }
        if (readed == null) {
            readed = false;
        }
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now(ZoneId.of("UTC"));
        }
        if (type == null) {
            type = "INFO";
        }
    }
}