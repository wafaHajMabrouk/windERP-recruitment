package com.winderp.authentification.services;

import com.winderp.authentification.Models.Admin;
import com.winderp.authentification.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE
    public Admin create(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    // READ ALL
    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    // READ BY ID
    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    // UPDATE
    public Admin update(Long id, Admin data) {
        Admin admin = getById(id);
        admin.setNom(data.getNom());
        admin.setEmail(data.getEmail());
        admin.setTelephone(data.getTelephone());
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return adminRepository.save(admin);
    }

    // DELETE
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }

    // ===========================
    // Vérifier si l'admin existe
    // ===========================
    public boolean existsById(Long id) {
        return adminRepository.existsById(id);
    }
}