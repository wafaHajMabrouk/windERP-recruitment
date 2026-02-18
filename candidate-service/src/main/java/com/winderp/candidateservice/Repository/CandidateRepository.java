package com.winderp.candidateservice.Repository;



import com.winderp.candidateservice.Models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    // Méthode pour rechercher un candidat par email
    Optional<Candidate> findByEmail(String email);
}
