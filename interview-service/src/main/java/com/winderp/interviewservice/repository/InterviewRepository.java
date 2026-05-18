package com.winderp.interviewservice.repository;

import com.winderp.interviewservice.models.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidatureId(Long candidatureId);
    List<Interview> findByRecruteurId(Long recruteurId);
    List<Interview> findByScoreGreaterThanEqual(Double score);
}
