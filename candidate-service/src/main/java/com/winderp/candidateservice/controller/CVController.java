package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.SERVICE.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;

    // Créer un CV
    @PostMapping
    public ResponseEntity<CV> createCV(@RequestBody CV cv) {
        CV savedCV = cvService.create(cv);
        return ResponseEntity.ok(savedCV);
    }

    // Récupérer tous les CVs
    @GetMapping
    public ResponseEntity<List<CV>> getAllCVs() {
        return ResponseEntity.ok(cvService.getByCandidateId(null)); // si tu veux tous, ajoute méthode getAll() dans CVService
    }

    // Récupérer CVs par Candidate ID
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CV>> getCVsByCandidate(@PathVariable Long candidateId) {
        List<CV> cvs = cvService.getByCandidateId(candidateId);
        return ResponseEntity.ok(cvs);
    }

    // Récupérer CVs par Offre ID
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<CV>> getCVsByOffre(@PathVariable Long offreId) {
        List<CV> cvs = cvService.getByOffreId(offreId);
        return ResponseEntity.ok(cvs);
    }

    // Supprimer un CV par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCV(@PathVariable Long id) {
        cvService.delete(id);
        return ResponseEntity.ok("CV supprimé avec succès !");
    }
}
