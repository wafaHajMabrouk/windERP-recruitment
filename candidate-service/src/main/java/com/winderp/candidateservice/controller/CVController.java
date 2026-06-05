package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.models.CV;
import com.winderp.candidateservice.models.Candidature;
import com.winderp.candidateservice.service.CVService;
import com.winderp.candidateservice.service.CandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CVController {

    private final CVService cvService;
    private final CandidatureService candidatureService;


    @PostMapping("/upload/{candidatureId}")
    public ResponseEntity<?> upload(@PathVariable Long candidatureId,
                                    @RequestParam("file") MultipartFile file) throws IOException {

        Candidature c = candidatureService.getById(candidatureId);

        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidature introuvable");
        }

        CV cv = cvService.uploadCV(file, c);
        return ResponseEntity.ok(cv);
    }


    @GetMapping("/download/by-candidature/{candidatureId}")
    public ResponseEntity<byte[]> download(@PathVariable Long candidatureId) {

        CV cv = cvService.getByCandidatureId(candidatureId);

        if (cv == null || cv.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + cv.getNomFichier() + "\"")
                .body(cv.getData());
    }
}