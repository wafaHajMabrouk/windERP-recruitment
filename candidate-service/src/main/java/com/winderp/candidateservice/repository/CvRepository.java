package com.winderp.candidateservice.repository;

import com.winderp.candidateservice.models.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvRepository extends JpaRepository<CV, Long> {

    // ✅ Retourne Optional pour pouvoir utiliser orElse()
    Optional<CV> findByCandidatureId(Long candidatureId);

}