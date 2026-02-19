package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.SERVICE.CandidatureService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;

    public CandidatureController(CandidatureService candidatureService) {
        this.candidatureService = candidatureService;
    }

    // ================= CREATE =================
    @PostMapping
    public Candidature addCandidature(@RequestBody Candidature candidature) {
        return candidatureService.save(candidature);
    }

    // ================= READ ALL =================
    @GetMapping
    public List<Candidature> getAllCandidatures() {
        return candidatureService.getAll();
    }

    // ================= READ BY CANDIDATE =================
    @GetMapping("/candidate/{id}")
    public List<Candidature> getByCandidate(@PathVariable Long id) {
        return candidatureService.getByCandidateId(id);
    }

    // ================= READ BY OFFRE =================
    @GetMapping("/offre/{id}")
    public List<Candidature> getByOffre(@PathVariable Long id) {
        return candidatureService.getByOffreId(id);
    }

    // ================= CHECK IF EXISTS =================
    @GetMapping("/exists/{id}")
    public Boolean exists(@PathVariable Long id) {
        return candidatureService.existsById(id);
    }
}
