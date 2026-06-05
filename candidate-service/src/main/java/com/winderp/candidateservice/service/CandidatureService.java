package com.winderp.candidateservice.service;

import com.winderp.candidateservice.client.AuthClient;
import com.winderp.candidateservice.models.Candidature;
import com.winderp.candidateservice.models.Offre;
import com.winderp.candidateservice.models.Status;
import com.winderp.candidateservice.models.Statut;
import com.winderp.candidateservice.repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j  // ✅ Logger au lieu de System.out
public class CandidatureService {

    private final CandidatureRepository repository;
    private final OffreService offreService;
    private final AuthClient authClient;
    private final EmailService emailService;

    // Constantes pour éviter les chaînes dupliquées (optionnel, mais bonne pratique)
    private static final String CANDIDATURE_INTROUVABLE = "Candidature introuvable id=";
    private static final String CANDIDAT_INCONNU = "Candidat inconnu";

    public Candidature create(Candidature c) {
        boolean exists = repository.existsByCandidateIdAndOffreId(
                c.getCandidateId(),
                c.getOffre().getId()
        );

        if (exists) {
            throw new IllegalArgumentException("Vous avez déjà postulé à cette offre");
        }

        Offre offre = offreService.getById(c.getOffre().getId());

        if (offre.getStatut() == Statut.FERME) {
            throw new IllegalStateException("Offre fermée");
        }

        c.setStatus(Status.EN_ATTENTE);
        c.setDecision("EN_ATTENTE");
        c.setScore(0.0);

        return repository.save(c);
    }

    public Candidature update(Long id, Candidature updated) {
        Candidature c = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(CANDIDATURE_INTROUVABLE + id));

        c.setOffre(updated.getOffre() != null ? updated.getOffre() : c.getOffre());
        c.setCandidateId(updated.getCandidateId() != null ? updated.getCandidateId() : c.getCandidateId());
        c.setStatus(updated.getStatus() != null ? updated.getStatus() : c.getStatus());
        c.setScore(updated.getScore() != null ? updated.getScore() : c.getScore());
        c.setDecision(updated.getDecision() != null ? updated.getDecision() : c.getDecision());

        Candidature saved = repository.save(c);
        envoyerEmailDecision(saved);
        return saved;
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new IllegalArgumentException(CANDIDATURE_INTROUVABLE + id);
        repository.deleteById(id);
    }

    public List<Candidature> getAll() {
        List<Candidature> list = repository.findAll();
        log.info("TOTAL CANDIDATURES DB = {}", list.size());
        list.forEach(c -> log.debug(" ID={} | score={} | decision={}", c.getId(), c.getScore(), c.getDecision()));
        return list;
    }

    public Candidature getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(CANDIDATURE_INTROUVABLE + id));
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
        } else if ("REFUSE".equalsIgnoreCase(decision)) {
            c.setStatus(Status.REFUSE);
        } else {
            c.setStatus(Status.EN_ATTENTE);
        }

        Candidature saved = repository.save(c);
        envoyerEmailDecision(saved);
        return saved;
    }

    private void envoyerEmailDecision(Candidature candidature) {
        try {
            String email = authClient.getUserEmail(candidature.getCandidateId());
            String nom = authClient.getUserName(candidature.getCandidateId());

            if (email != null && !email.isEmpty()) {
                emailService.envoyerResultatCandidature(candidature, email, nom);
                log.info("Email envoyé pour la candidature ID: {}", candidature.getId());
            } else {
                log.warn("Impossible d'envoyer l'email : adresse email non trouvée pour le candidat {}", candidature.getCandidateId());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email pour la candidature {}", candidature.getId(), e);
        }
    }

    public long count() {
        return repository.count();
    }

    public List<Map<String, Object>> getAcceptedCandidaturesForInterview() {
        List<Candidature> acceptedList = repository.findByStatus(Status.ACCEPTE);
        return acceptedList.stream().map(candidature -> {
            Map<String, Object> map = new HashMap<>();
            map.put("candidatureId", candidature.getId());

            String candidateName = CANDIDAT_INCONNU;
            try {
                String name = authClient.getUserName(candidature.getCandidateId());
                if (name != null && !name.trim().isEmpty()) {
                    candidateName = name;
                } else {
                    candidateName = "Candidat #" + candidature.getCandidateId();
                }
            } catch (Exception e) {
                log.warn("Erreur récupération nom candidat {}: {}", candidature.getCandidateId(), e.getMessage());
                candidateName = "Candidat #" + candidature.getCandidateId();
            }
            map.put("candidateName", candidateName);

            String offreTitre = "Offre inconnue";
            try {
                Offre offre = candidature.getOffre();
                if (offre != null && offre.getTitre() != null && !offre.getTitre().trim().isEmpty()) {
                    offreTitre = offre.getTitre();
                }
            } catch (Exception e) {
                log.warn("Erreur récupération offre pour candidature {}: {}", candidature.getId(), e.getMessage());
            }
            map.put("offreTitre", offreTitre);

            return map;
        }).collect(Collectors.toList());
    }

    public long countByStatus(Status status) {
        return repository.countByStatus(status);
    }

    public void renvoyerEmailManuellement(Long candidatureId) {
        Candidature c = getById(candidatureId);
        envoyerEmailDecision(c);
    }

    public String getCandidateName(Long candidateId) {
        try {
            return authClient.getUserName(candidateId);
        } catch (Exception e) {
            log.error("Auth service error: {}", e.getMessage());
            return "Candidat #" + candidateId;
        }
    }

    public Optional<Candidature> getByIdOptional(Long id) {
        return repository.findById(id);
    }

    public List<Candidature> filterByScore(Double minScore) {
        return repository.findByScoreGreaterThanEqual(minScore);
    }

    public List<Candidature> filterByScoreRange(Double min, Double max) {
        return repository.findByScoreBetween(min, max);
    }

    public List<Candidature> filterByScoreAndStatus(Double score, Status status) {
        return repository.findByScoreGreaterThanEqualAndStatus(score, status);
    }
}