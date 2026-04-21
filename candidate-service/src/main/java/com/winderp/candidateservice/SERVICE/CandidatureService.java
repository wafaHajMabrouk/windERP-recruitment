package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Client.AuthClient;
import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Models.Status;
import com.winderp.candidateservice.Models.Statut;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository repository;
    private final OffreService offreService; // AJOUTÉ
    private final AuthClient authClient;

    // CREATE : vérifie que l’offre est OUVERTE avant d’ajouter la candidature
    public Candidature create(Candidature c) {

        boolean exists = repository.existsByCandidateIdAndOffreId(
                c.getCandidateId(),
                c.getOffre().getId()
        );

        if (exists) {
            throw new RuntimeException("❌ Vous avez déjà postulé à cette offre");
        }

        Offre offre = offreService.getById(c.getOffre().getId());

        if (offre.getStatut() == Statut.FERME) {
            throw new RuntimeException("Offre fermée");
        }

        c.setStatus(Status.EN_ATTENTE);

        return repository.save(c);
    }

    public Candidature update(Long id, Candidature updated) {
        Candidature c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable id=" + id));
        c.setOffre(updated.getOffre() != null ? updated.getOffre() : c.getOffre());
        c.setCandidateId(updated.getCandidateId() != null ? updated.getCandidateId() : c.getCandidateId());
        c.setStatus(updated.getStatus() != null ? updated.getStatus() : c.getStatus());
        c.setScore(updated.getScore() != null ? updated.getScore() : c.getScore());
        c.setDecision(updated.getDecision() != null ? updated.getDecision() : c.getDecision());
        return repository.save(c);
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new RuntimeException("Candidature introuvable id=" + id);
        repository.deleteById(id);
    }

    public List<Candidature> getAll() {
        List<Candidature> list = repository.findAll();
        System.out.println("🔥 TOTAL CANDIDATURES DB = " + list.size());
        list.forEach(c -> System.out.println("👉 ID=" + c.getId() +
                " | score=" + c.getScore() +
                " | decision=" + c.getDecision()));
        return list;
    }

    public Candidature getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable id=" + id));
    }

    public List<Candidature> getByOffre(Long offreId) {
        return repository.findByOffreId(offreId);
    }

    @Transactional(readOnly = true)
    public List<Candidature> getByCandidate(Long candidateId) {
        List<Candidature> list = repository.findByCandidateId(candidateId);
        list.forEach(c -> {
            if (c.getCv() != null) {
                c.getCv().setContenuTexte(null);
            }
        });
        return list;
    }

    @Transactional
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

    public long count() {
        return repository.count();
    }
    public List<Map<String, Object>> getAcceptedCandidaturesForInterview() {
        List<Candidature> acceptedList = repository.findByStatus(Status.ACCEPTE);
        return acceptedList.stream().map(candidature -> {
            Map<String, Object> map = new HashMap<>();
            map.put("candidatureId", candidature.getId());

            String candidateName = "Candidat inconnu";
            try {
                String name = authClient.getUserName(candidature.getCandidateId());
                if (name != null && !name.trim().isEmpty()) {
                    candidateName = name;
                } else {
                    candidateName = "Candidat #" + candidature.getCandidateId();
                }
            } catch (Exception e) {
                candidateName = "Candidat #" + candidature.getCandidateId();
            }
            map.put("candidateName", candidateName);
            return map;
        }).collect(Collectors.toList());
    }
    public long countByStatus(Status status) {
        return repository.countByStatus(status);
    }

}