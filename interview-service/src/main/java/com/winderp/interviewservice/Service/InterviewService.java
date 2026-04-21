package com.winderp.interviewservice.Service;

import com.winderp.interviewservice.Client.CandidateClient;
import com.winderp.interviewservice.Client.RecruteurClient;
import com.winderp.interviewservice.Client.NotificationClient;
import com.winderp.interviewservice.Models.Interview;
import com.winderp.interviewservice.Repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository repository;
    private final CandidateClient candidateClient;
    private final RecruteurClient recruteurClient;
    private final NotificationClient notificationClient;

    private void enrichWithNames(Interview interview) {
        // Récupération du nom du candidat
        if (interview.getCandidatureId() != null) {
            try {
                String name = candidateClient.getCandidateNameByCandidatureId(interview.getCandidatureId());
                interview.setCandidateName(name != null && !name.isEmpty() ? name : "Candidat inconnu");
            } catch (Exception e) {
                interview.setCandidateName("Candidat inconnu");
            }
        }

        // Récupération du nom du recruteur
        if (interview.getRecruteurId() != null) {
            try {
                String name = recruteurClient.getRecruteurName(interview.getRecruteurId());
                interview.setRecruteurName(name != null && !name.isEmpty() ? name : "Recruteur inconnu");
            } catch (Exception e) {
                interview.setRecruteurName("Recruteur inconnu");
            }
        }
    }

    public Interview createInterview(Interview interview) {
        // Vérification des champs obligatoires
        if (interview.getCandidatureId() == null) {
            throw new IllegalArgumentException("CandidatureId est obligatoire");
        }
        if (interview.getRecruteurId() == null) {
            throw new IllegalArgumentException("RecruteurId est obligatoire");
        }

        // 1. Vérifier que la candidature existe
        Boolean exists = candidateClient.existsById(interview.getCandidatureId());
        if (exists == null || !exists) {
            throw new RuntimeException("Candidature inexistante : " + interview.getCandidatureId());
        }

        // 2. Vérifier que la candidature est acceptée (seules les candidatures acceptées peuvent avoir un entretien)
        Boolean isAccepted = candidateClient.isCandidatureAccepted(interview.getCandidatureId());
        if (isAccepted == null || !isAccepted) {
            throw new RuntimeException("Impossible de planifier un entretien : la candidature n'est pas acceptée (statut != ACCEPTEE)");
        }

        Interview saved = repository.save(interview);

        // Notification au candidat
        try {
            Long candidatId = candidateClient.getCandidatIdByCandidatureId(interview.getCandidatureId());
            if (candidatId != null) {
                String message = "📅 Entretien programmé le " + saved.getDateHeure()
                        + " (Type: " + saved.getType() + ")";
                notificationClient.sendNotification(candidatId, message);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur notification : " + e.getMessage());
        }

        return saved;
    }

    public List<Interview> getAll() {
        List<Interview> interviews = repository.findAll();
        interviews.forEach(this::enrichWithNames);
        return interviews;
    }

    public Interview getById(Long id) {
        Optional<Interview> optional = repository.findById(id);
        if (optional.isPresent()) {
            Interview interview = optional.get();
            enrichWithNames(interview);
            return interview;
        }
        return null;
    }

    public List<Interview> getByCandidatureId(Long candidatureId) {
        List<Interview> interviews = repository.findByCandidatureId(candidatureId);
        interviews.forEach(this::enrichWithNames);
        return interviews;
    }

    public List<Interview> getByRecruteurId(Long recruteurId) {
        List<Interview> interviews = repository.findByRecruteurId(recruteurId);
        interviews.forEach(this::enrichWithNames);
        return interviews;
    }

    public Interview save(Interview interview) {
        return repository.save(interview);
    }

    public boolean deleteInterviewById(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public long count() {
        return repository.count();
    }
}