package com.winderp.authentification.services;

import com.winderp.authentification.Models.Recruteur;
import com.winderp.authentification.Repository.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruteurService {

    private final RecruteurRepository recruteurRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE
    public Recruteur create(Recruteur recruteur) {
        recruteur.setPassword(passwordEncoder.encode(recruteur.getPassword()));
        return recruteurRepository.save(recruteur);
    }

    // READ ALL
    public List<Recruteur> getAll() {
        return recruteurRepository.findAll();
    }

    // READ BY ID
    public Optional<Recruteur> getById(Long id) {
        return recruteurRepository.findById(id);
    }

    // UPDATE
    public Recruteur update(Long id, Recruteur recruteur) {
        return recruteurRepository.findById(id).map(existing -> {
            if (recruteur.getNom() != null) existing.setNom(recruteur.getNom());
            if (recruteur.getPrenom() != null) existing.setPrenom(recruteur.getPrenom());
            if (recruteur.getEmail() != null) existing.setEmail(recruteur.getEmail());
            if (recruteur.getTelephone() != null) existing.setTelephone(recruteur.getTelephone());
            if (recruteur.getEntreprise() != null) existing.setEntreprise(recruteur.getEntreprise());
            if (recruteur.getPassword() != null && !recruteur.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(recruteur.getPassword()));
            }
            return recruteurRepository.save(existing);
        }).orElse(null);
    }

    // DELETE
    public void delete(Long id) {
        recruteurRepository.deleteById(id);
    }

    // FIND BY EMAIL
    public Optional<Recruteur> findByEmail(String email) {
        return recruteurRepository.findByEmail(email);
    }

    // EXISTS BY ID
    public boolean existsById(Long id) {
        return recruteurRepository.existsById(id);
    }
}