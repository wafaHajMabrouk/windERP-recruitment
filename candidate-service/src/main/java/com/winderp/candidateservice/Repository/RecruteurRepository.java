package com.winderp.candidateservice.Repository;



import com.winderp.candidateservice.Models.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {

    Optional<Recruteur> findByEmail(String email);

}

