package com.winderp.authentification.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CANDIDATE")
public class Candidate extends User {

    private String adresse;
    private String niveauExperience;
    private String competences;


}