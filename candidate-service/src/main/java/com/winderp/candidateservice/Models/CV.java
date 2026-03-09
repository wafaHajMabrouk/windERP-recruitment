package com.winderp.candidateservice.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;

    // 🔹 Candidate vient du auth-service → seulement ID
    @Column(nullable = false)
    private Long candidateId;

    // 🔹 Offre est dans le même microservice → relation JPA OK
    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;
}