package com.winderp.candidateservice.SERVICE;


import com.winderp.candidateservice.Models.Recruteur;
import com.winderp.candidateservice.Repository.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruteurService {

    private final RecruteurRepository recruteurRepository;

    public Recruteur create(Recruteur recruteur) {
        return recruteurRepository.save(recruteur);
    }

    public List<Recruteur> getAll() {
        return recruteurRepository.findAll();
    }

    public Recruteur getById(Long id) {
        return recruteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruteur not found"));
    }

    public Recruteur update(Long id, Recruteur data) {
        Recruteur r = getById(id);
        r.setNom(data.getNom());
        r.setEmail(data.getEmail());
        r.setPassword(data.getPassword());
        return recruteurRepository.save(r);
    }

    public void delete(Long id) {
        recruteurRepository.deleteById(id);
    }
}

