package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.Recruteur;
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

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Recruteur recruteur) {
        try {
            Recruteur saved = recruteurService.create(recruteur);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Recruteur>> getAll() {
        return ResponseEntity.ok(recruteurService.getAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Recruteur recruteur = recruteurService.getById(id);
            return ResponseEntity.ok(recruteur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        try {
            Recruteur updated = recruteurService.update(id, recruteur);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!recruteurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recruteur avec id " + id + " non trouvé");
        }
        recruteurService.delete(id);
        return ResponseEntity.ok("Recruteur avec id " + id + " supprimé avec succès");
    }

    // SEARCH BY EMAIL
    @GetMapping("/search")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return recruteurService.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Recruteur non trouvé"));
    }

    // EXISTS
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(recruteurService.existsById(id));
    }
}