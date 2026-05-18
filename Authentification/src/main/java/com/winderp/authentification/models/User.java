package com.winderp.authentification.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = Candidate.class, name = "CANDIDATE"),
        @JsonSubTypes.Type(value = RH.class, name = "RH"),
        @JsonSubTypes.Type(value = Recruteur.class, name = "RECRUTEUR")
})
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nom;
    private String prenom;

    @Column(nullable = false)
    private String password;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    // ✅ NOUVEAU
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING | APPROVED | REJECTED

    // champs factices
    public String getDepartement() { return null; }
    public void setDepartement(String departement) { }

    public String getAdresse() { return null; }
    public void setAdresse(String adresse) { }

    public String getCompetences() { return null; }
    public void setCompetences(String competences) { }

    public String getNiveauExperience() { return null; }
    public void setNiveauExperience(String niveauExperience) { }

    public String getNiveauResponsabilite() { return null; }
    public void setNiveauResponsabilite(String niveauResponsabilite) { }

    public String getEntreprise() { return null; }
    public void setEntreprise(String entreprise) { }

    public String getPoste() { return null; }
    public void setPoste(String poste) { }

    public String getSiteEntreprise() { return null; }
    public void setSiteEntreprise(String siteEntreprise) { }
}