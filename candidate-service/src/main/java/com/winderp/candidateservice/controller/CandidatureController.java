package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.*;
import com.winderp.candidateservice.SERVICE.*;
import com.winderp.candidateservice.ai.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final OffreService offreService;
    private final CVService cvService;
    private final AIService aiService;

    @PostMapping
    public Candidature create(@RequestBody Candidature candidature) {
        Offre offre = offreService.getById(candidature.getOffre().getId());
        candidature.setOffre(offre);
        return candidatureService.create(candidature);
    }

    @GetMapping("/offre/{offreId}")
    public List<Candidature> getByOffre(@PathVariable Long offreId) {
        return candidatureService.getByOffre(offreId);
    }

    @PostMapping("/{id}/cv")
    public CV uploadCV(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Candidature c = candidatureService.getById(id);
        return cvService.uploadCVFromPDF(file, c);  // ← utilisation correcte de l'extraction PDF
    }

    @PostMapping("/{id}/analyser")
    public AIResponse analyser(@PathVariable Long id) {
        Candidature c = candidatureService.getById(id);

        CV cv = cvService.getByCandidatureId(id);
        if (cv == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun CV associé");
        }

        String cvText = cv.getContenu();
        if (cvText == null || cvText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CV vide ou extraction échouée (PDF scanné ?)");
        }

        AIResponse response = aiService.analyserCV(c.getOffre().getDescription(), cvText);

        candidatureService.updateAfterAI(id, response.getScore(), response.getDecision());

        return response;
    }
}