package com.winderp.candidateservice.Repository;

import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByOffreId(Long offreId);
    List<Candidature> findByCandidateId(Long candidateId);

    // NOUVELLE MÉTHODE pour compter les candidatures d’une offre
    long countByOffreId(Long offreId);

    List<Candidature> findByStatus(Status status);
    boolean existsByCandidateIdAndOffreId(Long candidateId, Long offreId);

    long countByStatus(Status status);
}