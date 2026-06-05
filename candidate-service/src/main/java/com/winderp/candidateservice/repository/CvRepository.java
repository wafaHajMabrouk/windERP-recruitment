package com.winderp.candidateservice.repository;

import com.winderp.candidateservice.models.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvRepository extends JpaRepository<CV, Long> {


    Optional<CV> findByCandidatureId(Long candidatureId);

}