package com.winderp.candidateservice.repository;

import com.winderp.candidateservice.models.Candidature;
import com.winderp.candidateservice.models.Status;
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
    List<Candidature> findByScoreGreaterThanEqual(Double score);

    List<Candidature> findByScoreBetween(Double min, Double max);
    List<Candidature> findByScoreGreaterThanEqualAndStatus(Double score, Status status);
}