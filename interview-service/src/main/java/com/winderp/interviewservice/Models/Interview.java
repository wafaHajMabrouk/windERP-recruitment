package com.winderp.interviewservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long candidatureId;
    // ← Ne sera plus affiché dans le JSON
    private Long recruteurId;

    private String type;
    private String statut;

    private String dateHeure;

    private String feedback;
    private Double score;

    @Transient
    private String candidateName;

    @Transient
    private String recruteurName;
}