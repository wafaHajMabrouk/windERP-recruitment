package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.Repository.CvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CvRepository cvRepository;

    // Récupérer tous les CVs
    public List<CV> getAll() {
        return cvRepository.findAll();
    }

    // Récupérer par candidateId (pas de relation JPA vers Candidate)
    public List<CV> getByCandidateId(Long candidateId) {
        if (candidateId == null) {
            return cvRepository.findAll(); // si null, retourner tous
        }
        return cvRepository.findByCandidateId(candidateId);
    }

    // Récupérer par offreId
    public List<CV> getByOffreId(Long offreId) {
        return cvRepository.findByOffre_Id(offreId);
    }

    // Créer un CV
    public CV create(CV cv) {
        return cvRepository.save(cv);
    }

    // Supprimer un CV
    public void delete(Long id) {
        cvRepository.deleteById(id);
    }
}