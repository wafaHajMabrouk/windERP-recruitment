package com.winderp.authentification.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("RH")
public class RH extends User {

    private String departement;
    private String niveauResponsabilite;

}