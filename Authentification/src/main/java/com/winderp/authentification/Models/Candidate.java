package com.winderp.authentification.Models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.http.ResponseEntity;


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