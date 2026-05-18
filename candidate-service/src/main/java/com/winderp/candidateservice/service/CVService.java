package com.winderp.candidateservice.service;

import com.winderp.candidateservice.models.CV;
import com.winderp.candidateservice.models.Candidature;
import com.winderp.candidateservice.repository.CvRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CvRepository cvRepository;

    // =========================
    // GET CV
    // =========================
    @Transactional(readOnly = true)
    public CV getByCandidatureId(Long candidatureId) {
        return cvRepository.findByCandidatureId(candidatureId)
                .orElse(null);
    }

    // =========================
    // UPLOAD CV (PDF)
    // =========================
    public CV uploadCV(MultipartFile file, Candidature candidature) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Fichier vide !");
        }

        CV cv = cvRepository.findByCandidatureId(candidature.getId())
                .orElse(new CV());

        cv.setCandidature(candidature);
        cv.setNomFichier(file.getOriginalFilename());
        cv.setData(file.getBytes());
        cv.setContenuTexte(extractText(file));

        return cvRepository.save(cv);
    }

    // =========================
    // DELETE CV
    // =========================
    public void delete(Long id) {
        cvRepository.deleteById(id);
    }

    // =========================
    // PDF TEXT EXTRACTION
    // =========================
    private String extractText(MultipartFile file) {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(doc);
        } catch (Exception e) {
            return "";
        }
    }
    public CV uploadCVFromPDF(MultipartFile file, Candidature candidature) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Fichier vide !");
        }

        CV cv = cvRepository.findByCandidatureId(candidature.getId())
                .orElse(new CV());

        cv.setCandidature(candidature);
        cv.setNomFichier(file.getOriginalFilename());
        cv.setData(file.getBytes());

        // 🔥 FIX PDFBOX SAFE VERSION
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            cv.setContenuTexte(stripper.getText(document));
        } catch (Exception e) {
            cv.setContenuTexte("");
        }

        return cvRepository.save(cv);
    }
}