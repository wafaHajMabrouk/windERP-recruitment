package com.winderp.candidateservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "BYTEA")
    @JsonIgnore
    private byte[] data;

    @Column(columnDefinition = "TEXT")
    private String contenuTexte;

    @OneToOne
    @JoinColumn(name = "candidature_id", unique = true)
    private Candidature candidature;
}