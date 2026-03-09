package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.SERVICE.CandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Candidature> addCandidature(@RequestBody Candidature candidature) {
        Candidature saved = candidatureService.save(candidature);
        return ResponseEntity.ok(saved);
    }

    // ================= READ ALL =================
    @GetMapping
    public ResponseEntity<List<Candidature>> getAllCandidatures() {
        List<Candidature> candidatures = candidatureService.getAll();
        return ResponseEntity.ok(candidatures);
    }

    // ================= READ BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getById(@PathVariable Long id) {
        return candidatureService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= READ BY CANDIDATE =================
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Candidature>> getByCandidate(@PathVariable Long candidateId) {
        List<Candidature> candidatures = candidatureService.getByCandidateId(candidateId);
        return ResponseEntity.ok(candidatures);
    }

    // ================= READ BY OFFRE =================
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<Candidature>> getByOffre(@PathVariable Long offreId) {
        List<Candidature> candidatures = candidatureService.getByOffreId(offreId);
        return ResponseEntity.ok(candidatures);
    }

    // ================= CHECK IF EXISTS =================
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean exists = candidatureService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!candidatureService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        candidatureService.deleteById(id);
        return ResponseEntity.ok("Candidature supprimée avec succès !");

    }
    // ================= COUNT =================
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCandidatures() {
        long count = candidatureService.count();
        return ResponseEntity.ok(count);
    }}