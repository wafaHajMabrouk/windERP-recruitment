package com.winderp.interviewservice.controller;

import com.winderp.interviewservice.models.Interview;
import com.winderp.interviewservice.service.InterviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;  // ✅ OK pour Spring Boot 3.2.3
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // ✅ Utiliser @MockBean pour Spring Boot 3.2.3
    private InterviewService interviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Interview interview1;
    private Interview interview2;
    private List<Interview> interviewList;

    @BeforeEach
    void setUp() {
        // Interview 1
        interview1 = new Interview();
        interview1.setId(1L);
        interview1.setCandidatureId(100L);
        interview1.setRecruteurId(10L);
        interview1.setType("TECHNIQUE");
        interview1.setStatut("PLANIFIE");
        interview1.setDateHeure(LocalDateTime.now().plusDays(2).toString());
        interview1.setCandidateName("Jean Dupont");
        interview1.setRecruteurName("Pierre Martin");

        // Interview 2
        interview2 = new Interview();
        interview2.setId(2L);
        interview2.setCandidatureId(101L);
        interview2.setRecruteurId(10L);
        interview2.setType("RH");
        interview2.setStatut("TERMINE");
        interview2.setDateHeure(LocalDateTime.now().minusDays(1).toString());
        interview2.setFeedback("Très bon candidat");
        interview2.setScore(85.5);
        interview2.setCandidateName("Sophie Martin");
        interview2.setRecruteurName("Pierre Martin");

        interviewList = Arrays.asList(interview1, interview2);
    }

    @Test
    @DisplayName("GET /api/interviews - Récupérer tous les entretiens")
    void testGetAllInterviews_Success() throws Exception {
        when(interviewService.getAll()).thenReturn(interviewList);

        mockMvc.perform(get("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("TECHNIQUE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].score").value(85.5));

        verify(interviewService, times(1)).getAll();
    }

    @Test
    @DisplayName("POST /api/interviews - Créer un entretien")
    void testCreateInterview_Success() throws Exception {
        Interview newInterview = new Interview();
        newInterview.setCandidatureId(100L);
        newInterview.setRecruteurId(10L);
        newInterview.setType("TECHNIQUE");
        newInterview.setStatut("PLANIFIE");
        newInterview.setDateHeure(LocalDateTime.now().plusDays(2).toString());

        when(interviewService.createInterview(any(Interview.class))).thenReturn(interview1);
        when(interviewService.getById(1L)).thenReturn(interview1);

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newInterview)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/interviews/{id} - Par ID")
    void testGetInterviewById_Success() throws Exception {
        when(interviewService.getById(1L)).thenReturn(interview1);

        mockMvc.perform(get("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.candidateName").value("Jean Dupont"));
    }

    @Test
    @DisplayName("GET /api/interviews/candidature/{candidatureId}")
    void testGetByCandidature_Success() throws Exception {
        when(interviewService.getByCandidatureId(100L)).thenReturn(Arrays.asList(interview1));

        mockMvc.perform(get("/api/interviews/candidature/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/interviews/recruteur/{recruteurId}")
    void testGetByRecruteur_Success() throws Exception {
        when(interviewService.getByRecruteurId(10L)).thenReturn(interviewList);

        mockMvc.perform(get("/api/interviews/recruteur/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /api/interviews/{id}")
    void testDeleteInterview_Success() throws Exception {
        when(interviewService.deleteInterviewById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/interviews/count")
    void testGetTotalInterviews() throws Exception {
        when(interviewService.count()).thenReturn(5L);

        mockMvc.perform(get("/api/interviews/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("PUT /api/interviews/{id}/evaluation")
    void testAddEvaluation_Success() throws Exception {
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("score", 90.0);
        evaluation.put("feedback", "Excellent candidat");

        Interview updatedInterview = interview1;
        updatedInterview.setScore(90.0);
        updatedInterview.setFeedback("Excellent candidat");

        when(interviewService.getById(1L)).thenReturn(interview1);
        when(interviewService.save(any(Interview.class))).thenReturn(updatedInterview);

        mockMvc.perform(put("/api/interviews/1/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evaluation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(90.0))
                .andExpect(jsonPath("$.feedback").value("Excellent candidat"));
    }

    @Test
    @DisplayName("GET /api/interviews/filter/score")
    void testFilterByScore() throws Exception {
        List<Interview> highScoreInterviews = Arrays.asList(interview2);
        when(interviewService.getByMinScore(80.0)).thenReturn(highScoreInterviews);

        mockMvc.perform(get("/api/interviews/filter/score")
                        .param("minScore", "80.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].score").value(85.5));
    }
}