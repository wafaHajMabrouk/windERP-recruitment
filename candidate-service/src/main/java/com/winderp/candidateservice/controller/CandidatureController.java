package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.Models.Status;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import com.winderp.candidateservice.SERVICE.*;
import com.winderp.candidateservice.ai.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final OffreService offreService;
    private final CVService cvService;
    private final AIService aiService;
    private final CandidatureRepository repository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Candidature candidature) {
        try {
            candidature.setOffre(offreService.getById(candidature.getOffre().getId()));
            Candidature saved = candidatureService.create(candidature);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Candidature> getAllCandidatures() {
        try {
            List<Candidature> list = candidatureService.getAll();
            System.out.println("🔥 API retourne : " + list.size());
            return list;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Candidature getById(@PathVariable Long id) {
        try {
            return candidatureService.getById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidature introuvable id=" + id);
        }
    }

    @GetMapping("/offre/{offreId}")
    public List<Candidature> getByOffre(@PathVariable Long offreId) {
        return candidatureService.getByOffre(offreId);
    }

    @GetMapping("/candidate/{candidateId}")
    public List<Candidature> getByCandidate(@PathVariable Long candidateId) {
        try {
            return candidatureService.getByCandidate(candidateId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/candidateId")
    public Long getCandidateIdByCandidatureId(@PathVariable Long id) {
        Candidature c = candidatureService.getById(id);
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidature introuvable");
        }
        return c.getCandidateId();
    }

    @PostMapping("/{id}/cv")
    public CV uploadCV(@PathVariable Long id,
                       @RequestParam("file") MultipartFile file) throws IOException {

        Candidature c = candidatureService.getById(id);

        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidature introuvable");
        }

        return cvService.uploadCVFromPDF(file, c);
    }

    @PostMapping("/{id}/analyser")
    public Candidature analyser(@PathVariable Long id) {

        Candidature c = candidatureService.getById(id);
        CV cv = cvService.getByCandidatureId(id);

        if (cv == null || cv.getContenuTexte() == null || cv.getContenuTexte().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CV vide ou illisible");
        }

        try {
            AIResponse response = aiService.analyserCV(
                    c.getOffre().getDescription(),
                    cv.getContenuTexte()
            );

            return candidatureService.updateAfterAI(
                    id,
                    response.getScore(),
                    response.getDecision()
            );

        } catch (Exception e) {
            e.printStackTrace();

            return candidatureService.updateAfterAI(
                    id,
                    50.0,
                    "EN_ATTENTE"
            );
        }
    }

    @GetMapping("/exists/{id}")
    public Boolean existsById(@PathVariable Long id) {
        return repository.existsById(id);
    }

    @GetMapping("/count")
    public long count() {
        return candidatureService.count();
    }

    @PutMapping("/{id}")
    public Candidature update(@PathVariable Long id, @RequestBody Candidature updated) {
        return candidatureService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        candidatureService.delete(id);
    }

    @GetMapping("/isaccepted")
    public ResponseEntity<?> getAcceptedCandidaturesForInterview() {
        try {
            List<Map<String, Object>> result = candidatureService.getAcceptedCandidaturesForInterview();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/isAccepted")
    public ResponseEntity<Boolean> isCandidatureAccepted(@PathVariable Long id) {
        try {
            Candidature c = candidatureService.getById(id);
            if (c == null) return ResponseEntity.ok(false);
            boolean accepted = c.getStatus() == Status.ACCEPTE;
            return ResponseEntity.ok(accepted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/count/acceptees")
    public long countAccepted() {
        return candidatureService.countByStatus(Status.ACCEPTE);
    }

    // NOUVEAU ENDPOINT : Renvoyer l'email manuellement
    @PostMapping("/{id}/renvoyer-email")
    public ResponseEntity<String> renvoyerEmail(@PathVariable Long id) {
        try {
            candidatureService.renvoyerEmailManuellement(id);
            return ResponseEntity.ok("Email renvoyé avec succès pour la candidature " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/candidate-name")
    public ResponseEntity<String> getCandidateName(@PathVariable Long id) {
        try {
            return candidatureService.getByIdOptional(id)
                    .map(c -> {
                        String name = candidatureService.getCandidateName(c.getCandidateId());
                        return ResponseEntity.ok(name);
                    })
                    .orElse(ResponseEntity.ok("Candidat inconnu"));

        } catch (Exception e) {
            return ResponseEntity.ok("Candidat inconnu");
        }
    }
    @GetMapping("/filter/score")
    public List<Candidature> filterByScore(@RequestParam Double minScore) {
        return candidatureService.filterByScore(minScore);
    }
    // ✅ FILTER BY MIN SCORE


    // ✅ FILTER BY RANGE
    @GetMapping("/filter/score-range")
    public List<Candidature> filterByScoreRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return candidatureService.filterByScoreRange(min, max);
    }

    // ✅ FILTER BY SCORE + STATUS
    @GetMapping("/filter/score-status")
    public List<Candidature> filterByScoreAndStatus(
            @RequestParam Double score,
            @RequestParam Status status) {
        return candidatureService.filterByScoreAndStatus(score, status);
    }
}