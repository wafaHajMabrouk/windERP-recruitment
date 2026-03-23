package com.winderp.candidateservice.Repository;

import com.winderp.candidateservice.Models.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByOffreId(Long offreId);
}