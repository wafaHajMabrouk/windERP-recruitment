package com.winderp.authentification.controllers;

import com.winderp.authentification.models.Recruteur;
import com.winderp.authentification.services.RecruteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruteurs")
@RequiredArgsConstructor
@CrossOrigin("*")

public class RecruteurController {

    private final RecruteurService recruteurService;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Recruteur recruteur) {
        try {
            Recruteur saved = recruteurService.create(recruteur);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Recruteur>> getAll() {
        return ResponseEntity.ok(recruteurService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Recruteur recruteur = recruteurService.getById(id);
            return ResponseEntity.ok(recruteur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        try {
            Recruteur updated = recruteurService.update(id, recruteur);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!recruteurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recruteur avec id " + id + " non trouvé");
        }
        recruteurService.delete(id);
        return ResponseEntity.ok("Recruteur avec id " + id + " supprimé avec succès");
    }


    @GetMapping("/search")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return recruteurService.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Recruteur non trouvé"));
    }


    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(recruteurService.existsById(id));
    }
    @GetMapping("/{id}/name")
    public ResponseEntity<String> getRecruteurName(@PathVariable Long id) {
        try {
            Recruteur recruteur = recruteurService.getById(id);
            // Supposons que Recruteur a une méthode getNom() ou getFullName()
            String name = recruteur.getNom();        // adaptez selon votre entité
            return ResponseEntity.ok(name);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}