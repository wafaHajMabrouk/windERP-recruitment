package com.winderp.authentification.services;

import com.winderp.authentification.models.Admin;
import com.winderp.authentification.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;


    public Admin create(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }


    public List<Admin> getAll() {
        return adminRepository.findAll();
    }


    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }


    public Admin update(Long id, Admin data) {
        Admin admin = getById(id);
        if (data.getNom() != null) admin.setNom(data.getNom());
        if (data.getEmail() != null) admin.setEmail(data.getEmail());
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        if (data.getDepartement() != null) admin.setDepartement(data.getDepartement());
        return adminRepository.save(admin);
    }


    public void delete(Long id) {
        adminRepository.deleteById(id);
    }


    public boolean existsById(Long id) {
        return adminRepository.existsById(id);
    }
}