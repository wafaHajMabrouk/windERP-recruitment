package com.winderp.authentification.services;

import com.winderp.authentification.Models.Candidate;
import com.winderp.authentification.Repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE
    public Candidate create(Candidate candidate) {
        candidate.setPassword(passwordEncoder.encode(candidate.getPassword()));
        return candidateRepository.save(candidate);
    }

    // READ ALL
    public List<Candidate> getAll() {
        return candidateRepository.findAll();
    }

    // READ BY ID
    public Candidate getById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
    }

    // UPDATE
    public Candidate update(Long id, Candidate data) {
        Candidate candidate = getById(id);
        if (data.getNom() != null) candidate.setNom(data.getNom());
        if (data.getPrenom() != null) candidate.setPrenom(data.getPrenom());
        if (data.getEmail() != null) candidate.setEmail(data.getEmail());
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            candidate.setPassword(passwordEncoder.encode(data.getPassword()));
        }

        if (data.getAdresse() != null) candidate.setAdresse(data.getAdresse());
        if (data.getNiveauExperience() != null) candidate.setNiveauExperience(data.getNiveauExperience());
        if (data.getCompetences() != null) candidate.setCompetences(data.getCompetences());
        return candidateRepository.save(candidate);
    }

    // DELETE
    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }

    // FIND BY EMAIL
    public Optional<Candidate> findByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    // EXISTS
    public boolean existsById(Long id) {
        return candidateRepository.existsById(id);
    }

    // COUNT
    public long count() {
        return candidateRepository.count();
    }
}