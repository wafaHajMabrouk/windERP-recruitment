package com.winderp.interviewservice.service;

import com.winderp.interviewservice.client.CandidateClient;
import com.winderp.interviewservice.client.RecruteurClient;
import com.winderp.interviewservice.client.NotificationClient;
import com.winderp.interviewservice.client.AuthClient;
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

    // ✅ Constantes pour les chaînes dupliquées
    private static final String CANDIDAT_INCONNU = "Candidat inconnu";
    private static final String RECRUTEUR_INCONNU = "Recruteur inconnu";
    private static final String OFFRE_INCONNUE = "Offre inconnue";

    private final InterviewRepository repository;
    private final CandidateClient candidateClient;
    private final RecruteurClient recruteurClient;
    private final NotificationClient notificationClient;
    private final AuthClient authClient;

    @Autowired
    public InterviewService(InterviewRepository repository,
                            @Autowired(required = false) CandidateClient candidateClient,
                            @Autowired(required = false) RecruteurClient recruteurClient,
                            @Autowired(required = false) NotificationClient notificationClient,
                            @Autowired(required = false) AuthClient authClient) {
        this.repository = repository;
        this.candidateClient = candidateClient;
        this.recruteurClient = recruteurClient;
        this.notificationClient = notificationClient;
        this.authClient = authClient;
    }

    // ✅ Extraction : enrichissement nom candidat
    private void enrichCandidateName(Interview interview) {
        if (candidateClient == null || interview.getCandidatureId() == null) {
            interview.setCandidateName(CANDIDAT_INCONNU);
            return;
        }
        try {
            String name = candidateClient.getCandidateNameByCandidatureId(interview.getCandidatureId());
            interview.setCandidateName((name != null && !name.trim().isEmpty()) ? name : CANDIDAT_INCONNU);
        } catch (Exception e) {
            log.warn("Erreur nom candidat: {}", e.getMessage());
            interview.setCandidateName(CANDIDAT_INCONNU);
        }
    }

    // ✅ Extraction : enrichissement nom recruteur
    private void enrichRecruteurName(Interview interview) {
        if (recruteurClient == null || interview.getRecruteurId() == null) {
            interview.setRecruteurName(RECRUTEUR_INCONNU);
            return;
        }
        try {
            String name = recruteurClient.getRecruteurName(interview.getRecruteurId());
            interview.setRecruteurName(name != null ? name : RECRUTEUR_INCONNU);
        } catch (Exception e) {
            log.warn("Erreur nom recruteur: {}", e.getMessage());
            interview.setRecruteurName(RECRUTEUR_INCONNU);
        }
    }

    // ✅ Extraction : enrichissement titre offre
    private void enrichOffreTitre(Interview interview) {
        if (candidateClient == null || interview.getCandidatureId() == null) {
            interview.setOffreName(OFFRE_INCONNUE);
            return;
        }
        try {
            String offreTitre = candidateClient.getOffreTitreByCandidatureId(interview.getCandidatureId());
            interview.setOffreName(offreTitre != null && !offreTitre.trim().isEmpty() ? offreTitre : OFFRE_INCONNUE);
        } catch (Exception e) {
            log.warn("Impossible de récupérer l'offre pour candidature {} : {}", interview.getCandidatureId(), e.getMessage());
            interview.setOffreName(OFFRE_INCONNUE);
        }
    }

    // ✅ Méthode principale d'enrichissement (complexité réduite)
    private void enrichWithExternalData(Interview interview) {
        if (interview == null) return;
        enrichCandidateName(interview);
        enrichRecruteurName(interview);
        enrichOffreTitre(interview);
    }

    // ✅ Validation de la candidature (extraite pour réduire complexité de createInterview)
    private void validateCandidature(Long candidatureId) {
        if (candidateClient == null) {
            log.warn("CandidateClient non disponible – validation ignorée");
            return;
        }
        Boolean exists = candidateClient.existsById(candidatureId);
        if (exists == null || !exists) {
            throw new IllegalArgumentException("Candidature inexistante : " + candidatureId);
        }
        Boolean isAccepted = candidateClient.isCandidatureAccepted(candidatureId);
        if (isAccepted == null || !isAccepted) {
            throw new IllegalStateException("Impossible de planifier un entretien : la candidature n'est pas acceptée");
        }
    }

    // ✅ Envoi de notification (extraite)
    private void sendNotificationIfNeeded(Interview interview) {
        if (notificationClient == null || candidateClient == null) return;
        try {
            Long candidatId = candidateClient.getCandidatIdByCandidatureId(interview.getCandidatureId());
            if (candidatId != null) {
                String message = "Entretien programmé le " + interview.getDateHeure() + " (Type: " + interview.getType() + ")";
                notificationClient.sendNotification(candidatId, message);
            }
        } catch (Exception e) {
            log.error("Erreur notification : {}", e.getMessage());
        }
    }

    // ✅ createInterview refactorisée (complexité cognitive maintenant < 15)
    public Interview createInterview(Interview interview) {
        if (interview.getCandidatureId() == null) {
            throw new IllegalArgumentException("CandidatureId est obligatoire");
        }
        if (interview.getRecruteurId() == null) {
            throw new IllegalArgumentException("RecruteurId est obligatoire");
        }

        validateCandidature(interview.getCandidatureId());

        Interview saved = repository.save(interview);
        sendNotificationIfNeeded(saved);

        enrichWithExternalData(saved);
        return saved;
    }

    public List<Interview> getAll() {
        List<Interview> interviews = repository.findAll();
        interviews.forEach(this::enrichWithExternalData);
        return interviews;
    }

    // ✅ Retourne Optional au lieu de null
    public Optional<Interview> getById(Long id) {
        return repository.findById(id)
                .map(interview -> {
                    enrichWithExternalData(interview);
                    return interview;
                });
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