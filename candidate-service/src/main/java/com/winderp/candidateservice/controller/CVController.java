package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.SERVICE.CVService;
import com.winderp.candidateservice.SERVICE.CandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;
    private final CandidatureService candidatureService;

    @PostMapping("/upload/{candidatureId}")
    public CV uploadCV(@PathVariable Long candidatureId,
                       @RequestParam("file") MultipartFile file) throws IOException {
        Candidature candidature = candidatureService.getById(candidatureId);
        return cvService.uploadCV(file.getOriginalFilename(), String.valueOf(file), candidature);
    }

    @GetMapping("/{candidatureId}")
    public CV getCV(@PathVariable Long candidatureId) {
        return cvService.getByCandidatureId(candidatureId);
    }

    @DeleteMapping("/{candidatureId}")
    public String deleteCV(@PathVariable Long candidatureId) {
        CV cv = cvService.getByCandidatureId(candidatureId);
        cvService.delete(cv.getId());
        return "CV supprimé avec succès";
    }
}