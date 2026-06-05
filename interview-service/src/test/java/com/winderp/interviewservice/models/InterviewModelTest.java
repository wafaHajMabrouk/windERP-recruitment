package com.winderp.interviewservice.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterviewModelTest {

    private static final String FIXED_DATE = "2025-01-01T10:00:00";

    @Test
    void shouldCreateInterviewWithDefaultValues() {
        Interview interview = new Interview();
        assertNull(interview.getId());
        assertNull(interview.getCandidatureId());
        assertNull(interview.getDateHeure());
    }

    @Test
    void shouldSetAndGetFields() {
        Interview interview = new Interview();
        interview.setId(1L);
        interview.setCandidatureId(100L);
        interview.setRecruteurId(200L);
        interview.setDateHeure(FIXED_DATE);
        interview.setType("TECHNIQUE");
        interview.setScore(85.0);

        assertEquals(1L, interview.getId());
        assertEquals(100L, interview.getCandidatureId());
        assertEquals(200L, interview.getRecruteurId());
        assertEquals(FIXED_DATE, interview.getDateHeure());
        assertEquals("TECHNIQUE", interview.getType());
        assertEquals(85.0, interview.getScore());
    }
}