package com.winderp.candidateservice.Repository;

import com.winderp.candidateservice.Models.Offre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreRepository extends JpaRepository<Offre, Long> {
}