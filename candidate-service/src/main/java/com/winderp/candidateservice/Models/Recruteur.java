package com.winderp.candidateservice.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("RECRUTEUR")
@Entity
public class Recruteur extends User {

    private String telephone;
    private String entreprise;
}
