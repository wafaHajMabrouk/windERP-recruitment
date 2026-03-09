package com.winderp.candidateservice.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Candidate vient du auth-service → seulement ID
    @Column(nullable = false)
    private Long candidateId;

    // 🔹 Offre est dans le même microservice → relation JPA OK
    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    private LocalDate dateCandidature;

    private String statut; // EN_ATTENTE, ACCEPTE, REFUSE
}