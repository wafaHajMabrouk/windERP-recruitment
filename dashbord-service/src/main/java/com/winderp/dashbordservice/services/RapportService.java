package com.winderp.dashbordservice.services;

import com.winderp.dashbordservice.models.Rapport;
import com.winderp.dashbordservice.repository.RapportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;

    public Rapport createRapport(Rapport rapport){
        rapport.setDateCreation(LocalDate.now(ZoneId.of("UTC")));
        return rapportRepository.save(rapport);
    }

    public List<Rapport> getAllRapports(){
        return rapportRepository.findAll();
    }

    public void deleteRapport(Long id){
        rapportRepository.deleteById(id);
    }
}