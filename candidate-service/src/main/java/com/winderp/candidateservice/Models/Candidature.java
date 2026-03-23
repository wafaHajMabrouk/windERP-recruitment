package com.winderp.candidateservice.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId; // 🔥 vient du auth-service

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Double score;
    private String decision;
}