package com.winderp.candidateservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private Long candidateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offre_id")
    @JsonIgnoreProperties("candidatures")
    private Offre offre;

    private Double score;

    private String decision;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("candidature")
    private CV cv;
}