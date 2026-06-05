package com.winderp.interviewservice.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class InterviewModelTest {

    @Test
    void testInterviewBuilderAndGetters() {
        Interview interview = new Interview();
        interview.setId(1L);
        interview.setCandidatureId(100L);
        interview.setRecruteurId(200L);
        interview.setDateHeure(LocalDateTime.now());
        interview.setType("TECHNIQUE");
        interview.setScore(85.0);

        assertEquals(1L, interview.getId());
        assertEquals(100L, interview.getCandidatureId());
        assertEquals(200L, interview.getRecruteurId());
        assertNotNull(interview.getDateHeure());
        assertEquals("TECHNIQUE", interview.getType());
        assertEquals(85.0, interview.getScore());
    }

    @Test
    void testDefaultValues() {
        Interview interview = new Interview();
        assertNull(interview.getId());
        assertNull(interview.getCandidatureId());
        assertNull(interview.getDateHeure());
    }
}