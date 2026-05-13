package com.winderp.candidateservice.service;

import com.winderp.candidateservice.Client.AuthClient;
import com.winderp.candidateservice.Models.Candidature;
import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Models.Status;
import com.winderp.candidateservice.Models.Statut;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import com.winderp.candidateservice.SERVICE.CandidatureService;
import com.winderp.candidateservice.SERVICE.EmailService;
import com.winderp.candidateservice.SERVICE.OffreService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CandidatureServiceTest {

    @Mock
    private CandidatureRepository repository;

    @Mock
    private OffreService offreService;

    @Mock
    private AuthClient authClient;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CandidatureService service;

    private Candidature candidature;
    private Offre offre;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        offre = new Offre();
        offre.setId(1L);
        offre.setStatut(Statut.OUVERT);

        candidature = new Candidature();

        candidature.setId(1L);
        candidature.setCandidateId(100L);
        candidature.setOffre(offre);
        candidature.setScore(0.0);
        candidature.setDecision("EN_ATTENTE");
        candidature.setStatus(Status.EN_ATTENTE);
    }

    @Test
    void testCreateCandidature() {

        when(repository.existsByCandidateIdAndOffreId(100L, 1L))
                .thenReturn(false);

        when(offreService.getById(1L))
                .thenReturn(offre);

        when(repository.save(any(Candidature.class)))
                .thenReturn(candidature);

        Candidature result = service.create(candidature);

        assertNotNull(result);

        assertEquals(
                Status.EN_ATTENTE,
                result.getStatus()
        );

        verify(repository, times(1))
                .save(candidature);
    }

    @Test
    void testDuplicateCandidature() {

        when(repository.existsByCandidateIdAndOffreId(100L, 1L))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    service.create(candidature);
                });

        assertEquals(
                "❌ Vous avez déjà postulé à cette offre",
                exception.getMessage()
        );
    }

    @Test
    void testOffreFermee() {

        offre.setStatut(Statut.FERME);

        when(repository.existsByCandidateIdAndOffreId(100L, 1L))
                .thenReturn(false);

        when(offreService.getById(1L))
                .thenReturn(offre);

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    service.create(candidature);
                });

        assertEquals(
                "Offre fermée",
                exception.getMessage()
        );
    }

    @Test
    void testGetAll() {

        when(repository.findAll())
                .thenReturn(Arrays.asList(candidature));

        List<Candidature> list =
                service.getAll();

        assertEquals(1, list.size());

        verify(repository, times(1))
                .findAll();
    }

    @Test
    void testGetById() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(candidature));

        Candidature result =
                service.getById(1L);

        assertNotNull(result);

        assertEquals(
                100L,
                result.getCandidateId()
        );
    }

    @Test
    void testDelete() {

        when(repository.existsById(1L))
                .thenReturn(true);

        doNothing().when(repository)
                .deleteById(1L);

        service.delete(1L);

        verify(repository, times(1))
                .deleteById(1L);
    }

    @Test
    void testUpdateAfterAI() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(candidature));

        when(repository.save(any(Candidature.class)))
                .thenReturn(candidature);

        when(authClient.getUserEmail(100L))
                .thenReturn("wafa@gmail.com");

        when(authClient.getUserName(100L))
                .thenReturn("Wafa");

        Candidature result =
                service.updateAfterAI(
                        1L,
                        95.0,
                        "ACCEPTE"
                );

        assertEquals(
                95.0,
                result.getScore()
        );

        assertEquals(
                Status.ACCEPTE,
                result.getStatus()
        );

        verify(emailService, times(1))
                .envoyerResultatCandidature(
                        any(),
                        anyString(),
                        anyString()
                );
    }
}