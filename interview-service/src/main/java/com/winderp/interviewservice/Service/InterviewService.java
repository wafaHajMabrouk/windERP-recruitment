package com.winderp.interviewservice.Service;

import com.winderp.interviewservice.Client.CandidateClient;
import com.winderp.interviewservice.Models.Interview;
import com.winderp.interviewservice.Repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository repository;
    private final CandidateClient candidateClient;

    public InterviewService(InterviewRepository repository, CandidateClient candidateClient) {
        this.repository = repository;
        this.candidateClient = candidateClient;
    }

    // Créer un entretien après vérification que la candidature existe
    public Interview createInterview(Interview interview) {
        if (interview.getCandidatureId() == null) {
            throw new IllegalArgumentException("CandidatureId ne peut pas être null");
        }

        Boolean exists = candidateClient.existsById(interview.getCandidatureId());
        if (exists == null || !exists) {
            throw new RuntimeException("Candidature inexistante : " + interview.getCandidatureId());
        }

        return repository.save(interview);
    }

    // Récupérer tous les entretiens
    public List<Interview> getAll() {
        return repository.findAll();
    }

    // Récupérer par candidature
    public List<Interview> getByCandidatureId(Long id) {
        return repository.findByCandidatureId(id);
    }

    // Récupérer par recruteur
    public List<Interview> getByRecruteurId(Long id) {
        return repository.findByRecruteurId(id);
    }

    // Supprimer par ID
    public boolean deleteInterviewById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
