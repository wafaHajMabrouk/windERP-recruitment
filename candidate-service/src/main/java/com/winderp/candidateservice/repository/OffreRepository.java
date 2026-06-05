package com.winderp.candidateservice.repository;

import com.winderp.candidateservice.models.Offre;
import com.winderp.candidateservice.models.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {


    List<Offre> findByCategorie(String categorie);


    List<Offre> findByMotCleContainingIgnoreCase(String motCle);


    List<Offre> findByStatut(Statut statut);


    List<Offre> findByCategorieAndMotCleContainingIgnoreCase(String categorie, String motCle);


    @Query("SELECT o FROM Offre o WHERE o.statut = :statut AND (o.dateLimite IS NULL OR o.dateLimite >= CURRENT_DATE)")
    List<Offre> findOffresOuvertes(@Param("statut") Statut statut);

    long countByStatut(Statut statut);
}