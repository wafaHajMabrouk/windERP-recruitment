package com.winderp.interviewservice.Repository;

import com.winderp.interviewservice.Models.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidatureId(Long candidatureId);
    List<Interview> findByRecruteurId(Long recruteurId);
}
