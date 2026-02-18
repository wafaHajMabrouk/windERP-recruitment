package com.winderp.candidateservice.Repository;



import com.winderp.candidateservice.Models.RH;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RHRepository extends JpaRepository<RH, Long> {

    Optional<RH> findByEmail(String email);

}

