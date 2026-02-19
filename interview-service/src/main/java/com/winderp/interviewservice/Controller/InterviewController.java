package com.winderp.interviewservice.Controller;

import com.winderp.interviewservice.Models.Interview;
import com.winderp.interviewservice.Service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<?> createInterview(@RequestBody Interview interview) {
        try {
            Interview created = interviewService.createInterview(interview);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Interview>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAll());
    }

    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<List<Interview>> getByCandidature(@PathVariable Long candidatureId) {
        return ResponseEntity.ok(interviewService.getByCandidatureId(candidatureId));
    }

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<Interview>> getByRecruteur(@PathVariable Long recruteurId) {
        return ResponseEntity.ok(interviewService.getByRecruteurId(recruteurId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInterview(@PathVariable Long id) {
        boolean deleted = interviewService.deleteInterviewById(id);
        if (deleted) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview non trouvée avec ID: " + id);
    }
}
