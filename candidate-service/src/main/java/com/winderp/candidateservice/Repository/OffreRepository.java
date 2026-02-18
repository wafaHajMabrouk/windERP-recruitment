package com.winderp.candidateservice.Repository;


import com.winderp.candidateservice.Models.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {

    List<Offre> findByLocalisation(String localisation);

    List<Offre> findByTitreContaining(String keyword);

}
