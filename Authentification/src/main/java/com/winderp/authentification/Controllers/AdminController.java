package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.Admin;
import com.winderp.authentification.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public Admin create(@RequestBody Admin admin) {
        return adminService.create(admin);
    }

    @GetMapping
    public List<Admin> getAll() {
        return adminService.getAll();
    }

    @GetMapping("/{id}")
    public Admin getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @PutMapping("/{id}")
    public Admin update(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.update(id, admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!adminService.existsById(id)) {
            return ResponseEntity.status(404).body("Admin avec id " + id + " non trouvé");
        }
        adminService.delete(id);
        return ResponseEntity.ok("Admin avec id " + id + " supprimé avec succès");
    }
}