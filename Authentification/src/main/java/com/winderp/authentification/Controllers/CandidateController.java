package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.Candidate;
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

    // ================= CREATE =================
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

    // ================= READ ALL =================
    @GetMapping
    public ResponseEntity<List<Candidate>> getAll() {
        return ResponseEntity.ok(candidateService.getAll());
    }

    // ================= READ BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable Long id) {
        return candidateService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Candidate candidate) {
        try {
            Candidate updated = candidateService.update(id, candidate);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!candidateService.existsById(id)) {
            return ResponseEntity.status(404).body("Candidate avec id " + id + " non trouvée");
        }
        candidateService.delete(id);
        return ResponseEntity.ok("Candidate avec id " + id + " supprimée avec succès");
    }

    // ================= SEARCH BY EMAIL =================
    @GetMapping("/search")
    public ResponseEntity<Candidate> getByEmail(@RequestParam String email) {
        return candidateService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= CHECK EXISTS =================
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.existsById(id));
    }

    // ================= COUNT =================
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(candidateService.count());
    }
}