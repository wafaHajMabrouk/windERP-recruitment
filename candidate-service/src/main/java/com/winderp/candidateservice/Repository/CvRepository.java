package com.winderp.candidateservice.Repository;

import com.winderp.candidateservice.Models.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CvRepository extends JpaRepository<CV, Long> {

    // Utilise candidateId au lieu de candidate
    List<CV> findByCandidateId(Long candidateId);

    // Pour les offres (relation ManyToOne)
    List<CV> findByOffre_Id(Long offreId);
}