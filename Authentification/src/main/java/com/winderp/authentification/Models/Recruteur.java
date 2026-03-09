package com.winderp.authentification.Models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("RECRUTEUR")
public class Recruteur extends User {

    private String telephone;
    private String entreprise;

}