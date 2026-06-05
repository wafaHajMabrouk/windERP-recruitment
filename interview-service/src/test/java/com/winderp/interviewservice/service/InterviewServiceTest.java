package com.winderp.interviewservice.service;

import com.winderp.interviewservice.client.CandidateClient;
import com.winderp.interviewservice.client.NotificationClient;
import com.winderp.interviewservice.client.RecruteurClient;
import com.winderp.interviewservice.client.AuthClient;
import com.winderp.interviewservice.models.Interview;
import com.winderp.interviewservice.repository.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, Month.JANUARY, 15, 10, 0);

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private CandidateClient candidateClient;

    @Mock
    private RecruteurClient recruteurClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private InterviewService interviewService;

    private Interview interview;
    private List<Interview> interviewList;

    @BeforeEach
    void setUp() {
        interview = new Interview();
        interview.setId(1L);
        interview.setCandidatureId(100L);
        interview.setRecruteurId(10L);
        interview.setType("TECHNIQUE");
        interview.setStatut("PLANIFIE");
        interview.setDateHeure(FIXED_DATE.plusDays(2).toString());

        Interview interview2 = new Interview();
        interview2.setId(2L);
        interview2.setCandidatureId(101L);
        interview2.setRecruteurId(10L);
        interview2.setType("RH");
        interview2.setStatut("TERMINE");
        interview2.setScore(85.5);

        interviewList = Arrays.asList(interview, interview2);
    }

    @Test
    @DisplayName("createInterview - Création avec succès")
    void testCreateInterview_Success() {
        when(candidateClient.existsById(100L)).thenReturn(true);
        when(candidateClient.isCandidatureAccepted(100L)).thenReturn(true);
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        when(candidateClient.getCandidatIdByCandidatureId(100L)).thenReturn(1L);

        Interview result = interviewService.createInterview(interview);

        assertNotNull(result);
        assertEquals(100L, result.getCandidatureId());
        verify(interviewRepository, times(1)).save(interview);
        verify(notificationClient, times(1)).sendNotification(eq(1L), anyString());
    }

    @Test
    @DisplayName("createInterview - Échec: candidature inexistante")
    void testCreateInterview_CandidatureNotFound() {
        when(candidateClient.existsById(100L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interviewService.createInterview(interview);
        });

        assertEquals("Candidature inexistante : 100", exception.getMessage());
        verify(interviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("createInterview - Échec: candidature non acceptée")
    void testCreateInterview_CandidatureNotAccepted() {
        when(candidateClient.existsById(100L)).thenReturn(true);
        when(candidateClient.isCandidatureAccepted(100L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interviewService.createInterview(interview);
        });

        assertEquals("Impossible de planifier un entretien : la candidature n'est pas acceptée", exception.getMessage());
        verify(interviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("createInterview - Échec: candidatureId null")
    void testCreateInterview_CandidatureIdNull() {
        interview.setCandidatureId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            interviewService.createInterview(interview);
        });

        assertEquals("CandidatureId est obligatoire", exception.getMessage());
    }

    @Test
    @DisplayName("createInterview - Échec: recruteurId null")
    void testCreateInterview_RecruteurIdNull() {
        interview.setRecruteurId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            interviewService.createInterview(interview);
        });

        assertEquals("RecruteurId est obligatoire", exception.getMessage());
    }

    @Test
    @DisplayName("getAll - Récupérer tous les entretiens")
    void testGetAll() {
        when(interviewRepository.findAll()).thenReturn(interviewList);
        when(candidateClient.getCandidateNameByCandidatureId(100L)).thenReturn("Jean Dupont");
        when(recruteurClient.getRecruteurName(10L)).thenReturn("Pierre Martin");

        List<Interview> result = interviewService.getAll();

        assertEquals(2, result.size());
        verify(interviewRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getById - Récupérer entretien par ID avec succès (retourne Optional)")
    void testGetById_Success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(candidateClient.getCandidateNameByCandidatureId(100L)).thenReturn("Jean Dupont");
        when(recruteurClient.getRecruteurName(10L)).thenReturn("Pierre Martin");

        Optional<Interview> result = interviewService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Jean Dupont", result.get().getCandidateName());
    }

    @Test
    @DisplayName("getById - Entretien non trouvé (retourne Optional vide)")
    void testGetById_NotFound() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Interview> result = interviewService.getById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("getByCandidatureId - Récupérer par candidature")
    void testGetByCandidatureId() {
        when(interviewRepository.findByCandidatureId(100L)).thenReturn(Arrays.asList(interview));
        when(candidateClient.getCandidateNameByCandidatureId(100L)).thenReturn("Jean Dupont");
        when(recruteurClient.getRecruteurName(10L)).thenReturn("Pierre Martin");

        List<Interview> result = interviewService.getByCandidatureId(100L);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getCandidatureId());
    }

    @Test
    @DisplayName("getByRecruteurId - Récupérer par recruteur")
    void testGetByRecruteurId() {
        when(interviewRepository.findByRecruteurId(10L)).thenReturn(interviewList);

        List<Interview> result = interviewService.getByRecruteurId(10L);

        assertEquals(2, result.size());
        verify(interviewRepository, times(1)).findByRecruteurId(10L);
    }

    @Test
    @DisplayName("deleteInterviewById - Suppression avec succès")
    void testDeleteInterviewById_Success() {
        when(interviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(interviewRepository).deleteById(1L);

        boolean result = interviewService.deleteInterviewById(1L);

        assertTrue(result);
        verify(interviewRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteInterviewById - Entretien non trouvé")
    void testDeleteInterviewById_NotFound() {
        when(interviewRepository.existsById(99L)).thenReturn(false);

        boolean result = interviewService.deleteInterviewById(99L);

        assertFalse(result);
        verify(interviewRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("count - Compter les entretiens")
    void testCount() {
        when(interviewRepository.count()).thenReturn(5L);

        long result = interviewService.count();

        assertEquals(5L, result);
        verify(interviewRepository, times(1)).count();
    }

    @Test
    @DisplayName("save - Sauvegarder entretien")
    void testSave() {
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        Interview result = interviewService.save(interview);

        assertNotNull(result);
        verify(interviewRepository, times(1)).save(interview);
    }

    @Test
    @DisplayName("getByMinScore - Filtrer par score minimum")
    void testGetByMinScore() {
        List<Interview> highScoreInterviews = Arrays.asList(interviewList.get(1));
        when(interviewRepository.findByScoreGreaterThanEqual(80.0)).thenReturn(highScoreInterviews);

        List<Interview> result = interviewService.getByMinScore(80.0);

        assertEquals(1, result.size());
        assertEquals(85.5, result.get(0).getScore());
        verify(interviewRepository, times(1)).findByScoreGreaterThanEqual(80.0);
    }
}