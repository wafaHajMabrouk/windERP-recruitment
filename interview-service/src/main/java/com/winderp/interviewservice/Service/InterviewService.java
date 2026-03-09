package com.winderp.interviewservice.Service;

import com.winderp.interviewservice.Client.CandidateClient;
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

    // ================= CREATE =================
    public Interview createInterview(Interview interview) {
        if (interview.getCandidatureId() == null) {
            throw new IllegalArgumentException("CandidatureId ne peut pas être null");
        }

        // Vérifier que la candidature existe via CandidateClient
        Boolean exists = candidateClient.existsById(interview.getCandidatureId());
        if (exists == null || !exists) {
            throw new RuntimeException("Candidature inexistante : " + interview.getCandidatureId());
        }

        return repository.save(interview);
    }

    // ================= READ ALL =================
    public List<Interview> getAll() {
        return repository.findAll();
    }

    // ================= READ BY ID =================
    public Interview getById(Long id) {
        Optional<Interview> optional = repository.findById(id);
        return optional.orElse(null);
    }

    // ================= READ BY CANDIDATURE =================
    public List<Interview> getByCandidatureId(Long candidatureId) {
        return repository.findByCandidatureId(candidatureId);
    }

    // ================= READ BY RECRUTEUR =================
    public List<Interview> getByRecruteurId(Long recruteurId) {
        return repository.findByRecruteurId(recruteurId);
    }

    // ================= DELETE =================
    public boolean deleteInterviewById(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
    public long count() {
        return repository.count();
    }
}