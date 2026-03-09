package com.winderp.authentification.services;

import com.winderp.authentification.Models.RH;
import com.winderp.authentification.Repository.RHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RHService {

    private final RHRepository rhRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= CREATE =================
    public RH createRH(RH rh) {
        // Encoder le mot de passe avant sauvegarde
        rh.setPassword(passwordEncoder.encode(rh.getPassword()));
        return rhRepository.save(rh);
    }

    // ================= READ ALL =================
    public List<RH> getAllRH() {
        return rhRepository.findAll();
    }

    // ================= READ BY ID =================
    public RH getRHById(Long id) {
        return rhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RH not found"));
    }

    // ================= UPDATE =================
    public RH updateRH(Long id, RH rhDetails) {
        RH rh = getRHById(id);
        if (rhDetails.getNom() != null) rh.setNom(rhDetails.getNom());
        if (rhDetails.getEmail() != null) rh.setEmail(rhDetails.getEmail());
        if (rhDetails.getTelephone() != null) rh.setTelephone(rhDetails.getTelephone());
        if (rhDetails.getPassword() != null && !rhDetails.getPassword().isEmpty()) {
            rh.setPassword(passwordEncoder.encode(rhDetails.getPassword()));
        }
        return rhRepository.save(rh);
    }

    // ================= DELETE =================
    public void deleteRH(Long id) {
        rhRepository.deleteById(id);
    }

    // ================= EXISTS BY ID =================
    public boolean existsById(Long id) {
        return rhRepository.existsById(id);
    }
}