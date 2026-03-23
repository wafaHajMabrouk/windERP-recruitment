package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository repository;

    public Offre create(Offre offre) {
        return repository.save(offre);
    }

    public List<Offre> getAll() {
        return repository.findAll();
    }

    public Offre getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));
    }
}