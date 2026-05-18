package com.winderp.authentification.controllers;

import com.winderp.authentification.models.Candidate;
import com.winderp.authentification.services.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@CrossOrigin("*")

public class CandidateController {

    private final CandidateService candidateService;

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Candidate candidate) {
        if (candidate.getEmail() == null || candidate.getEmail().isEmpty()
                || candidate.getPassword() == null || candidate.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Email et mot de passe obligatoires");
        }
        try {
            Candidate saved = candidateService.create(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Candidate>> getAll() {
        return ResponseEntity.ok(candidateService.getAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Candidate candidate = candidateService.getById(id);
            return ResponseEntity.ok(candidate);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Candidate candidate) {
        try {
            Candidate updated = candidateService.update(id, candidate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!candidateService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidate avec id " + id + " non trouvée");
        }
        candidateService.delete(id);
        return ResponseEntity.ok("Candidate avec id " + id + " supprimée avec succès");
    }

    // SEARCH BY EMAIL
    @GetMapping("/search")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return candidateService.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Candidate non trouvée"));
    }

    // EXISTS
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.existsById(id));
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(candidateService.count());
    }
}
