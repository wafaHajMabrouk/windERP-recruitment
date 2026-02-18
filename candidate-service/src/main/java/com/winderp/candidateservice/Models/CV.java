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

    private String nomFichier; // nom du CV ou path

    // Relation vers Candidate
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    // Relation vers Offre
    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;
}
