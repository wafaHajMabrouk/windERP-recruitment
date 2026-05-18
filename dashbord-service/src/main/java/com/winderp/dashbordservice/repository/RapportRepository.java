package com.winderp.dashbordservice.repository;


import com.winderp.dashbordservice.models.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RapportRepository extends JpaRepository<Rapport, Long> {
}
