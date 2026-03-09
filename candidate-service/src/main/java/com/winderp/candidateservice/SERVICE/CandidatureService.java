package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Client.CandidateClient;
import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import com.winderp.candidateservice.Repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final CandidateClient candidateClient; // Feign client pour auth-service
    private final OffreRepository offreRepository;

    // ================= CREATE =================
    public Candidature save(Candidature candidature) {
        // Vérifier existence du candidat via auth-service
        Map<String, Object> candidateData = candidateClient.getUserById(candidature.getCandidateId());
        if (candidateData == null || candidateData.isEmpty()) {
            throw new RuntimeException("Candidate not found with id " + candidature.getCandidateId());
        }

        // Vérifier existence de l'offre locale
        Offre offre = offreRepository.findById(candidature.getOffre().getId())
                .orElseThrow(() -> new RuntimeException("Offre not found with id " + candidature.getOffre().getId()));

        candidature.setOffre(offre);

        // Définir la date de candidature si non fournie
        if (candidature.getDateCandidature() == null) {
            candidature.setDateCandidature(LocalDate.now());
        }

        return candidatureRepository.save(candidature);
    }

    // ================= READ =================
    public List<Candidature> getAll() {
        return candidatureRepository.findAll();
    }

    public Optional<Candidature> getById(Long id) {
        return candidatureRepository.findById(id);
    }

    public List<Candidature> getByCandidateId(Long candidateId) {
        return candidatureRepository.findByCandidateId(candidateId);
    }

    public List<Candidature> getByOffreId(Long offreId) {
        return candidatureRepository.findByOffreId(offreId);
    }

    // ================= CHECK =================
    public boolean existsById(Long id) {
        return candidatureRepository.existsById(id);
    }

    // ================= DELETE =================
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new RuntimeException("Candidature not found with id " + id);
        }
        candidatureRepository.deleteById(id);
    }
    public long count() {
        return candidatureRepository.count();
    }
}