package com.winderp.authentification.services;

import com.winderp.authentification.models.RH;
import com.winderp.authentification.repository.RHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RHService {

    private final RHRepository rhRepository;
    private final PasswordEncoder passwordEncoder;


    public RH create(RH rh) {
        rh.setPassword(passwordEncoder.encode(rh.getPassword()));
        return rhRepository.save(rh);
    }


    public List<RH> getAll() {
        return rhRepository.findAll();
    }


    public RH getById(Long id) {
        return rhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RH not found with id: " + id));
    }


    public RH update(Long id, RH data) {
        RH rh = getById(id);
        if (data.getNom() != null) rh.setNom(data.getNom());
        if (data.getEmail() != null) rh.setEmail(data.getEmail());

        if (data.getDepartement() != null) rh.setDepartement(data.getDepartement());
        if (data.getNiveauResponsabilite() != null) rh.setNiveauResponsabilite(data.getNiveauResponsabilite());
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            rh.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return rhRepository.save(rh);
    }


    public void delete(Long id) {
        rhRepository.deleteById(id);
    }


    public boolean existsById(Long id) {
        return rhRepository.existsById(id);
    }
}