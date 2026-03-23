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

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contenu; // Stocke le texte extrait du PDF

    @OneToOne
    private Candidature candidature;
}