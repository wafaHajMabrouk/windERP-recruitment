package com.winderp.authentification.services;

import com.winderp.authentification.models.Candidate;
import com.winderp.authentification.repository.CandidateRepository;
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


    public Candidate create(Candidate candidate) {
        candidate.setPassword(passwordEncoder.encode(candidate.getPassword()));
        return candidateRepository.save(candidate);
    }


    public List<Candidate> getAll() {
        return candidateRepository.findAll();
    }


    public Candidate getById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
    }


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


    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }


    public Optional<Candidate> findByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }


    public boolean existsById(Long id) {
        return candidateRepository.existsById(id);
    }


    public long count() {
        return candidateRepository.count();
    }
}