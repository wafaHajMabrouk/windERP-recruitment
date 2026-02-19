package com.winderp.interviewservice.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidatureId;
    private Long recruteurId;
    private String type;
    private String statut;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Calendar dateHeure;

    private String feedback;
    private Integer score;
}
