package com.winderp.interviewservice.service;

import com.winderp.interviewservice.client.CandidateClient;
import com.winderp.interviewservice.client.RecruteurClient;
import com.winderp.interviewservice.client.NotificationClient;
import com.winderp.interviewservice.client.authClient;
import com.winderp.interviewservice.models.Interview;
import com.winderp.interviewservice.repository.InterviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InterviewService {

    private final InterviewRepository repository;
    private final CandidateClient candidateClient;
    private final RecruteurClient recruteurClient;
    private final NotificationClient notificationClient;
    private final authClient authClient;

    @Autowired
    public InterviewService(InterviewRepository repository,
                            @Autowired(required = false) CandidateClient candidateClient,
                            @Autowired(required = false) RecruteurClient recruteurClient,
                            @Autowired(required = false) NotificationClient notificationClient,
                            @Autowired(required = false) authClient authClient) {
        this.repository = repository;
        this.candidateClient = candidateClient;
        this.recruteurClient = recruteurClient;
        this.notificationClient = notificationClient;
        this.authClient = authClient;
    }

    // ✅ Une seule méthode pour tout enrichir
    private void enrichWithExternalData(Interview interview) {
        if (interview == null) return;

        // 1. Nom du candidat
        if (candidateClient != null && interview.getCandidatureId() != null) {
            try {
                String name = candidateClient.getCandidateNameByCandidatureId(interview.getCandidatureId());
                interview.setCandidateName((name != null && !name.trim().isEmpty()) ? name : "Candidat inconnu");
            } catch (Exception e) {
                log.warn("Erreur nom candidat: {}", e.getMessage());
                interview.setCandidateName("Candidat inconnu");
            }
        } else {
            interview.setCandidateName("Candidat inconnu");
        }

        // 2. Nom du recruteur
        if (recruteurClient != null && interview.getRecruteurId() != null) {
            try {
                String name = recruteurClient.getRecruteurName(interview.getRecruteurId());
                interview.setRecruteurName(name != null ? name : "Recruteur inconnu");
            } catch (Exception e) {
                log.warn("Erreur nom recruteur: {}", e.getMessage());
                interview.setRecruteurName("Recruteur inconnu");
            }
        } else {
            interview.setRecruteurName("Recruteur inconnu");
        }

        // 3. ✅ Titre de l'offre (NOUVEAU)
        if (candidateClient != null && interview.getCandidatureId() != null) {
            try {
                String offreTitre = candidateClient.getOffreTitreByCandidatureId(interview.getCandidatureId());
                interview.setOffreName(offreTitre != null && !offreTitre.trim().isEmpty() ? offreTitre : "Offre inconnue");
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'offre pour candidature {} : {}", interview.getCandidatureId(), e.getMessage());
                interview.setOffreName("Offre inconnue");
            }
        } else {
            interview.setOffreName("Offre inconnue");
        }
    }

    public Interview createInterview(Interview interview) {
        if (interview.getCandidatureId() == null) {
            throw new IllegalArgumentException("CandidatureId est obligatoire");
        }
        if (interview.getRecruteurId() == null) {
            throw new IllegalArgumentException("RecruteurId est obligatoire");
        }

        if (candidateClient != null) {
            Boolean exists = candidateClient.existsById(interview.getCandidatureId());
            if (exists == null || !exists) {
                throw new RuntimeException("Candidature inexistante : " + interview.getCandidatureId());
            }

            Boolean isAccepted = candidateClient.isCandidatureAccepted(interview.getCandidatureId());
            if (isAccepted == null || !isAccepted) {
                throw new RuntimeException("Impossible de planifier un entretien : la candidature n'est pas acceptée");
            }
        } else {
            log.warn("CandidateClient non disponible – validation ignorée");
        }

        Interview saved = repository.save(interview);

        if (notificationClient != null) {
            try {
                if (candidateClient != null) {
                    Long candidatId = candidateClient.getCandidatIdByCandidatureId(interview.getCandidatureId());
                    if (candidatId != null) {
                        String message = "Entretien programmé le " + saved.getDateHeure() + " (Type: " + saved.getType() + ")";
                        notificationClient.sendNotification(candidatId, message);
                    }
                }
            } catch (Exception e) {
                log.error("Erreur notification : {}", e.getMessage());
            }
        }

        // ✅ Enrichir avant retour
        enrichWithExternalData(saved);
        return saved;
    }

    public List<Interview> getAll() {
        List<Interview> interviews = repository.findAll();
        interviews.forEach(this::enrichWithExternalData);
        return interviews;
    }

    public Interview getById(Long id) {
        Optional<Interview> optional = repository.findById(id);
        if (optional.isPresent()) {
            Interview interview = optional.get();
            enrichWithExternalData(interview);
            return interview;
        }
        return null;
    }

    public List<Interview> getByCandidatureId(Long candidatureId) {
        List<Interview> interviews = repository.findByCandidatureId(candidatureId);
        interviews.forEach(this::enrichWithExternalData);
        return interviews;
    }

    public List<Interview> getByRecruteurId(Long recruteurId) {
        List<Interview> interviews = repository.findByRecruteurId(recruteurId);
        interviews.forEach(this::enrichWithExternalData);
        return interviews;
    }

    public Interview save(Interview interview) {
        Interview saved = repository.save(interview);
        enrichWithExternalData(saved);
        return saved;
    }

    public boolean deleteInterviewById(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public long count() {
        return repository.count();
    }

    public List<Interview> getByMinScore(Double score) {
        List<Interview> interviews = repository.findByScoreGreaterThanEqual(score);
        interviews.forEach(this::enrichWithExternalData);
        return interviews;
    }
}