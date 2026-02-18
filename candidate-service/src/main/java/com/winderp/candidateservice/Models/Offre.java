package com.winderp.candidateservice.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String statut;         // ex: Ouvert / Fermé
    private String localisation;   // <-- ajouté pour permettre la recherche

}
