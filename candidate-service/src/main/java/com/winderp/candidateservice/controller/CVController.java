package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Client.CandidateClient;
import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.SERVICE.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;
    private final CandidateClient candidateClient; // Feign Client pour auth-service

    // Créer un CV
    @PostMapping
    public ResponseEntity<CV> createCV(@RequestBody CV cv) {
        // Optionnel : vérifier que le candidateId existe dans auth-service
        if (cv.getCandidateId() != null) {
            Map<String, Object> candidate = candidateClient.getUserById(cv.getCandidateId());
            if (candidate == null || candidate.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        CV savedCV = cvService.create(cv);

        // ⚡ Supprimer les relations vers Offre pour éviter crash JSON
        savedCV.setOffre(null);

        return ResponseEntity.ok(savedCV);
    }

    // Récupérer tous les CVs
    @GetMapping
    public ResponseEntity<List<CV>> getAllCVs() {
        List<CV> cvs = cvService.getAll();
        cvs.forEach(cv -> cv.setOffre(null)); // Supprimer les offres pour JSON
        return ResponseEntity.ok(cvs);
    }

    // Récupérer CVs par Candidate ID
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CV>> getCVsByCandidate(@PathVariable Long candidateId) {
        List<CV> cvs = cvService.getByCandidateId(candidateId);
        cvs.forEach(cv -> cv.setOffre(null));
        return ResponseEntity.ok(cvs);
    }

    // Récupérer CVs par Offre ID
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<CV>> getCVsByOffre(@PathVariable Long offreId) {
        List<CV> cvs = cvService.getByOffreId(offreId);
        cvs.forEach(cv -> cv.setOffre(null));
        return ResponseEntity.ok(cvs);
    }

    // Supprimer un CV par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCV(@PathVariable Long id) {
        cvService.delete(id);
        return ResponseEntity.ok("CV supprimé avec succès !");
    }
}