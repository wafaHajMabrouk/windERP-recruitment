package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.Candidate;
import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import com.winderp.candidateservice.Repository.CandidateRepository;
import com.winderp.candidateservice.Repository.OffreRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final CandidateRepository candidateRepository;
    private final OffreRepository offreRepository;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              CandidateRepository candidateRepository,
                              OffreRepository offreRepository) {
        this.candidatureRepository = candidatureRepository;
        this.candidateRepository = candidateRepository;
        this.offreRepository = offreRepository;
    }

    // ================= CREATE =================
    public Candidature save(Candidature candidature) {
        // Vérifier et récupérer le candidat existant
        Candidate candidate = candidateRepository.findById(candidature.getCandidate().getId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Vérifier et récupérer l'offre existante
        Offre offre = offreRepository.findById(candidature.getOffre().getId())
                .orElseThrow(() -> new RuntimeException("Offre not found"));

        // Lier les entités existantes à la candidature
        candidature.setCandidate(candidate);
        candidature.setOffre(offre);

        return candidatureRepository.save(candidature);
    }

    // ================= READ =================
    public List<Candidature> getAll() {
        return candidatureRepository.findAll();
    }

    public List<Candidature> getByCandidateId(Long candidateId) {
        return candidatureRepository.findByCandidateId(candidateId);
    }

    public List<Candidature> getByOffreId(Long offreId) {
        return candidatureRepository.findByOffreId(offreId);
    }

    // ================= CHECK EXISTS =================
    public boolean existsById(Long id) {
        return candidatureRepository.existsById(id);
    }
}
