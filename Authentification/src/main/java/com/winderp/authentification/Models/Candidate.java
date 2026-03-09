package com.winderp.authentification.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@DiscriminatorValue("CANDIDATE")
@Table(name = "candidate")
public class Candidate extends User {

    private String telephone;
    private String adresse;
}