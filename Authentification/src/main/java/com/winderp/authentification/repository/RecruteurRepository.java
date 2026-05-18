package com.winderp.authentification.repository;

import com.winderp.authentification.models.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {

    Optional<Recruteur> findByEmail(String email);

}
