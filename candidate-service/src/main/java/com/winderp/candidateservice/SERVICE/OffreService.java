package com.winderp.candidateservice.SERVICE;



import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository offreRepository;

    public Offre create(Offre offre) {
        return offreRepository.save(offre);
    }

    public List<Offre> getAll() {
        return offreRepository.findAll();
    }

    public Offre getById(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre not found"));
    }

    public Offre update(Long id, Offre data) {
        Offre offre = getById(id);
        offre.setTitre(data.getTitre());
        offre.setDescription(data.getDescription());
        offre.setLocalisation(data.getLocalisation());
        return offreRepository.save(offre);
    }

    public void delete(Long id) {
        offreRepository.deleteById(id);
    }

    public List<Offre> searchByLocalisation(String localisation) {
        return offreRepository.findByLocalisation(localisation);
    }

    public List<Offre> searchByKeyword(String keyword) {
        return offreRepository.findByTitreContaining(keyword);
    }
}
