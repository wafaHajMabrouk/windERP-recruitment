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
    public Recruteur getById(Long id) {
        return recruteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruteur not found with id: " + id));
    }

    // UPDATE
    public Recruteur update(Long id, Recruteur data) {
        Recruteur recruteur = getById(id);
        if (data.getNom() != null) recruteur.setNom(data.getNom());
        if (data.getPrenom() != null) recruteur.setPrenom(data.getPrenom());
        if (data.getEmail() != null) recruteur.setEmail(data.getEmail());
        if (data.getEntreprise() != null) recruteur.setEntreprise(data.getEntreprise());
        if (data.getPoste() != null) recruteur.setPoste(data.getPoste());
        if (data.getSiteEntreprise() != null) recruteur.setSiteEntreprise(data.getSiteEntreprise());
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            recruteur.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return recruteurRepository.save(recruteur);
    }

    // DELETE
    public void delete(Long id) {
        recruteurRepository.deleteById(id);
    }

    // FIND BY EMAIL
    public Optional<Recruteur> findByEmail(String email) {
        return recruteurRepository.findByEmail(email);
    }

    // EXISTS
    public boolean existsById(Long id) {
        return recruteurRepository.existsById(id);
    }
}