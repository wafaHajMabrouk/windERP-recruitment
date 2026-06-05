package com.winderp.interviewservice.controller;

import com.winderp.interviewservice.models.Interview;
import com.winderp.interviewservice.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class InterviewController {

    private static final String INTERVIEW_NOT_FOUND = "Interview non trouvée";
    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<?> createInterview(@RequestBody Interview interview) {
        try {
            Interview created = interviewService.createInterview(interview);
            // created est déjà enrichi par le service, pas besoin de rappeler getById
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            log.error("Erreur lors de la création de l'interview", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : " + ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Interview>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInterviewById(@PathVariable Long id) {
        Optional<Interview> interviewOpt = interviewService.getById(id);
        if (interviewOpt.isPresent()) {
            return ResponseEntity.ok(interviewOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(INTERVIEW_NOT_FOUND);
        }
    }

    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<List<Interview>> getByCandidature(@PathVariable Long candidatureId) {
        return ResponseEntity.ok(interviewService.getByCandidatureId(candidatureId));
    }

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<Interview>> getByRecruteur(@PathVariable Long recruteurId) {
        List<Interview> list = interviewService.getByRecruteurId(recruteurId);
        log.info("INTERVIEWS FOUND = {}", list.size());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInterview(@PathVariable Long id) {
        boolean deleted = interviewService.deleteInterviewById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(INTERVIEW_NOT_FOUND);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalInterviews() {
        return ResponseEntity.ok(interviewService.count());
    }

    @PutMapping("/{id}/evaluation")
    public ResponseEntity<?> addEvaluation(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Optional<Interview> interviewOpt = interviewService.getById(id);
        if (interviewOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(INTERVIEW_NOT_FOUND);
        }
        Interview interview = interviewOpt.get();
        if (payload.containsKey("score")) {
            interview.setScore(Double.valueOf(payload.get("score").toString()));
        }
        if (payload.containsKey("feedback")) {
            interview.setFeedback(payload.get("feedback").toString());
        }
        Interview updated = interviewService.save(interview);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter/score")
    public ResponseEntity<List<Interview>> filterByScore(@RequestParam Double minScore) {
        return ResponseEntity.ok(interviewService.getByMinScore(minScore));
    }
}