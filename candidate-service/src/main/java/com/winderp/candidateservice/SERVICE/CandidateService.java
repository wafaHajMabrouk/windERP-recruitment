package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.Candidate;
import com.winderp.candidateservice.Repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository repository;

    // ================= CREATE =================
    public Candidate create(Candidate candidate) {
        return repository.save(candidate);
    }

    // ================= READ ALL =================
    public List<Candidate> getAll() {
        return repository.findAll();
    }

    // ================= READ BY ID =================
    public Candidate getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
    }

    // ================= UPDATE =================
    public Candidate update(Long id, Candidate updatedCandidate) {
        Candidate existing = getById(id);

        // Propriétés héritées de User
        existing.setNom(updatedCandidate.getNom());
        existing.setEmail(updatedCandidate.getEmail());
        existing.setPassword(updatedCandidate.getPassword());

        // Propriétés spécifiques à Candidate
        existing.setTelephone(updatedCandidate.getTelephone());
        existing.setAdresse(updatedCandidate.getAdresse());

        return repository.save(existing);
    }

    // ================= DELETE =================
    public void delete(Long id) {
        Candidate existing = getById(id);
        repository.delete(existing);
    }

    // ================= SEARCH BY EMAIL =================
    public Candidate findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidate not found with email: " + email));
    }

    // ================= CHECK IF EXISTS =================
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
