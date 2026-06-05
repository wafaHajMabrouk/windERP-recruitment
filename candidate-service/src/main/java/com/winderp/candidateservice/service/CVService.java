package com.winderp.candidateservice.service;

import com.winderp.candidateservice.models.CV;
import com.winderp.candidateservice.models.Candidature;
import com.winderp.candidateservice.repository.CvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVService {

    private final CvRepository cvRepository;

    @Transactional(readOnly = true)
    public CV getByCandidatureId(Long candidatureId) {
        return cvRepository.findByCandidatureId(candidatureId)
                .orElse(null);
    }

    public CV uploadCV(MultipartFile file, Candidature candidature) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide !");
        }

        CV cv = cvRepository.findByCandidatureId(candidature.getId())
                .orElse(new CV());

        cv.setCandidature(candidature);
        cv.setNomFichier(file.getOriginalFilename());
        cv.setData(file.getBytes());
        cv.setContenuTexte(extractText(file));

        return cvRepository.save(cv);
    }

    public void delete(Long id) {
        cvRepository.deleteById(id);
    }

    private String extractText(MultipartFile file) {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(doc);
        } catch (Exception e) {
            log.warn("Extraction de texte impossible pour le fichier {}", file.getOriginalFilename(), e);
            return "";
        }
    }

    public CV uploadCVFromPDF(MultipartFile file, Candidature candidature) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide !");
        }

        CV cv = cvRepository.findByCandidatureId(candidature.getId())
                .orElse(new CV());

        cv.setCandidature(candidature);
        cv.setNomFichier(file.getOriginalFilename());
        cv.setData(file.getBytes());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            cv.setContenuTexte(stripper.getText(document));
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du PDF pour la candidature {}", candidature.getId(), e);
            cv.setContenuTexte("");
        }

        return cvRepository.save(cv);
    }
}