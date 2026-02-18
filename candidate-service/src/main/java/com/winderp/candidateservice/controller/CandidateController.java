package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.Candidate;
import com.winderp.candidateservice.SERVICE.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Candidate> create(@RequestBody Candidate candidate) {
        Candidate saved = candidateService.create(candidate);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ================= READ ALL =================
    @GetMapping
    public ResponseEntity<List<Candidate>> getAll() {
        List<Candidate> candidates = candidateService.getAll();
        return ResponseEntity.ok(candidates);
    }

    // ================= READ BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable Long id) {
        Candidate candidate = candidateService.getById(id);
        return ResponseEntity.ok(candidate);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Candidate> update(@PathVariable Long id, @RequestBody Candidate candidate) {
        Candidate updated = candidateService.update(id, candidate);
        return ResponseEntity.ok(updated);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ================= SEARCH BY EMAIL =================
    @GetMapping("/search")
    public ResponseEntity<Candidate> getByEmail(@RequestParam String email) {
        Candidate candidate = candidateService.findByEmail(email);
        return ResponseEntity.ok(candidate);
    }
}
