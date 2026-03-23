package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.*;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository repository;

    public Candidature create(Candidature c) {
        c.setStatus(Status.EN_ATTENTE);
        return repository.save(c);
    }

    public List<Candidature> getByOffre(Long offreId) {
        return repository.findByOffreId(offreId);
    }

    public Candidature getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));
    }

    public Candidature updateAfterAI(Long id, double score, String decision) {
        Candidature c = getById(id);
        c.setScore(score);
        c.setDecision(decision);
        if ("ACCEPTE".equalsIgnoreCase(decision)) {
            c.setStatus(Status.ACCEPTE);
        } else {
            c.setStatus(Status.REFUSE);
        }
        return repository.save(c);
    }
}