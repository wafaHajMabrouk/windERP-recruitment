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

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    private LocalDate dateCandidature;

    private String statut; // ex: "en attente", "accepté", "refusé"

    // Getters et setters
}
