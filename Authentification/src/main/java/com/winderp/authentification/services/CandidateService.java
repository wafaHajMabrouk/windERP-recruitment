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

    // ================= CREATE =================
    public Candidate create(Candidate candidate) {
        candidate.setPassword(passwordEncoder.encode(candidate.getPassword()));
        return candidateRepository.save(candidate);
    }

    // ================= READ ALL =================
    public List<Candidate> getAll() {
        return candidateRepository.findAll();
    }

    // ================= READ BY ID =================
    public Optional<Candidate> getById(Long id) {
        return candidateRepository.findById(id);
    }

    // ================= UPDATE =================
    public Candidate update(Long id, Candidate candidate) {
        return candidateRepository.findById(id).map(c -> {
            if (candidate.getNom() != null) c.setNom(candidate.getNom());
            if (candidate.getPrenom() != null) c.setPrenom(candidate.getPrenom());
            if (candidate.getEmail() != null) c.setEmail(candidate.getEmail());
            if (candidate.getPassword() != null && !candidate.getPassword().isEmpty()) {
                c.setPassword(passwordEncoder.encode(candidate.getPassword()));
            }
            if (candidate.getTelephone() != null) c.setTelephone(candidate.getTelephone());
            if (candidate.getAdresse() != null) c.setAdresse(candidate.getAdresse());
            return candidateRepository.save(c);
        }).orElse(null);
    }

    // ================= DELETE =================
    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }

    // ================= FIND BY EMAIL =================
    public Optional<Candidate> findByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    // ================= CHECK EXISTS =================
    public boolean existsById(Long id) {
        return candidateRepository.existsById(id);
    }

    // ================= COUNT =================
    public long count() {
        return candidateRepository.count();
    }
}