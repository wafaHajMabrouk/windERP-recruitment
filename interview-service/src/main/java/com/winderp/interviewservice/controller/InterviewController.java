package com.winderp.interviewservice.controller;

import com.winderp.interviewservice.models.Interview;
import com.winderp.interviewservice.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<?> createInterview(@RequestBody Interview interview) {
        try {
            Interview created = interviewService.createInterview(interview);
            Interview response = interviewService.getById(created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview non trouvée");
        }
        return ResponseEntity.ok(interview);
    }

    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<List<Interview>> getByCandidature(@PathVariable Long candidatureId) {
        return ResponseEntity.ok(interviewService.getByCandidatureId(candidatureId));
    }

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<Interview>> getByRecruteur(@PathVariable Long recruteurId) {

        List<Interview> list = interviewService.getByRecruteurId(recruteurId);

        System.out.println("🔥 INTERVIEWS FOUND = " + list.size());

        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInterview(@PathVariable Long id) {
        boolean deleted = interviewService.deleteInterviewById(id);
        if (deleted) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview non trouvée");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalInterviews() {
        return ResponseEntity.ok(interviewService.count());
    }

    @PutMapping("/{id}/evaluation")
    public ResponseEntity<?> addEvaluation(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Interview interview = interviewService.getById(id);
        if (interview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview non trouvée");
        }
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