package com.winderp.authentification.repository;


import com.winderp.authentification.models.RH;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RHRepository extends JpaRepository<RH, Long> {

    Optional<RH> findByEmail(String email);

}
