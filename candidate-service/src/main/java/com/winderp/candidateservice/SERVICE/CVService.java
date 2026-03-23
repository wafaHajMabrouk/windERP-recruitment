package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Repository.CvRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CVService {

    private static final Logger log = LoggerFactory.getLogger(CVService.class);

    private final CvRepository cvRepository;

    // ✅ CREATE OU UPDATE CV
    @Transactional
    public CV uploadCV(String filename, String textContent, Candidature candidature) {

        Optional<CV> existingCV = cvRepository.findByCandidatureId(candidature.getId());

        CV cv;

        if (existingCV.isPresent()) {
            // 🔥 UPDATE
            cv = existingCV.get();
            log.info("Mise à jour du CV pour candidature {}", candidature.getId());
        } else {
            // 🔥 CREATE
            cv = new CV();
            cv.setCandidature(candidature);
            log.info("Création d'un nouveau CV pour candidature {}", candidature.getId());
        }

        cv.setNomFichier(filename);
        cv.setContenu(textContent != null ? textContent.trim() : "");

        return cvRepository.save(cv);
    }

    // ✅ Upload PDF (utilise la méthode au-dessus)
    @Transactional
    public CV uploadCVFromPDF(MultipartFile file, Candidature candidature) throws IOException {
        String textContent = extractTextFromPDF(file);
        return uploadCV(file.getOriginalFilename(), textContent, candidature);
    }

    // ✅ GET CV
    @Transactional(readOnly = true)
    public CV getByCandidatureId(Long candidatureId) {
        return cvRepository.findByCandidatureId(candidatureId).orElse(null);
    }

    // ✅ DELETE CV
    @Transactional
    public void delete(Long cvId) {
        cvRepository.deleteById(cvId);
    }

    // ✅ EXTRACTION PDF (OK)
    private String extractTextFromPDF(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {

            byte[] pdfBytes = is.readAllBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);

                String extractedText = stripper.getText(document);

                log.info("Texte extrait du PDF '{}': {} caractères",
                        file.getOriginalFilename(),
                        extractedText != null ? extractedText.length() : 0);

                return extractedText != null ? extractedText.trim() : "";
            }

        } catch (IOException e) {
            log.error("Erreur extraction PDF '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
            return "";
        }
    }
}