package com.winderp.candidateservice.SERVICE;


import com.winderp.candidateservice.Models.Admin;
import com.winderp.candidateservice.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin create(Admin admin) {
        return adminRepository.save(admin);
    }

    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin update(Long id, Admin data) {
        Admin admin = getById(id);
        admin.setNom(data.getNom());
        admin.setEmail(data.getEmail());
        admin.setPassword(data.getPassword());
        return adminRepository.save(admin);
    }

    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}

