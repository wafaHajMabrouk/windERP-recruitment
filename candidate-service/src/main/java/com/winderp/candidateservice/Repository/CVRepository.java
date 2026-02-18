package com.winderp.candidateservice.Repository;

import com.winderp.candidateservice.Models.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CVRepository extends JpaRepository<CV, Long> {

    // Accéder à l'ID de la relation Candidate
    List<CV> findByCandidate_Id(Long candidateId);

    // Accéder à l'ID de la relation Offre
    List<CV> findByOffre_Id(Long offreId);
}
