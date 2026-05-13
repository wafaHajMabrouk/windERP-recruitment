package com.winderp.dashbordservice.Services;

import com.winderp.dashbordservice.Client.*;
import com.winderp.dashbordservice.Models.DashboardStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private CandidatureClient candidatureClient;

    @Mock
    private InterviewClient interviewClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // Configuration initiale si nécessaire
    }

    @Test
    @DisplayName("getStats - Récupération des statistiques avec succès")
    void testGetStats_Success() {
        // Given
        when(candidatureClient.getTotalCandidatures()).thenReturn(100);
        when(candidatureClient.getAcceptedCandidatures()).thenReturn(40);
        when(authClient.getTotalCandidates()).thenReturn(25);
        when(interviewClient.getTotalInterviews()).thenReturn(15);
        when(candidatureClient.getOffresOuvertesCount()).thenReturn(10);
        when(candidatureClient.getOffresFermeesCount()).thenReturn(5);
        when(notificationClient.getTotalNotifications()).thenReturn(80);

        // When
        DashboardStats stats = dashboardService.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(100, stats.getTotalCandidatures());
        assertEquals(40, stats.getCandidaturesAcceptees());
        assertEquals(25, stats.getCandidaturesNouvelles());
        assertEquals(15, stats.getEntretiensPlanifies());
        assertEquals(10, stats.getOffresOuvertes());
        assertEquals(5, stats.getOffresFermees());
        assertEquals(80, stats.getNotificationsEnvoyees());
        assertEquals(40.0, stats.getTauxAcceptation());

        // Verify
        verify(candidatureClient, times(1)).getTotalCandidatures();
        verify(candidatureClient, times(1)).getAcceptedCandidatures();
        verify(authClient, times(1)).getTotalCandidates();
        verify(interviewClient, times(1)).getTotalInterviews();
        verify(candidatureClient, times(1)).getOffresOuvertesCount();
        verify(candidatureClient, times(1)).getOffresFermeesCount();
        verify(notificationClient, times(1)).getTotalNotifications();
    }

    @Test
    @DisplayName("getStats - Taux d'acceptation calculé correctement")
    void testGetStats_TauxAcceptationCalculation() {
        // Given
        when(candidatureClient.getTotalCandidatures()).thenReturn(50);
        when(candidatureClient.getAcceptedCandidatures()).thenReturn(25);
        when(authClient.getTotalCandidates()).thenReturn(10);
        when(interviewClient.getTotalInterviews()).thenReturn(5);
        when(candidatureClient.getOffresOuvertesCount()).thenReturn(8);
        when(candidatureClient.getOffresFermeesCount()).thenReturn(3);
        when(notificationClient.getTotalNotifications()).thenReturn(40);

        // When
        DashboardStats stats = dashboardService.getStats();

        // Then
        assertEquals(50.0, stats.getTauxAcceptation()); // (25/50)*100 = 50%
    }

    @Test
    @DisplayName("getStats - Taux d'acceptation à 0 quand total candidatures = 0")
    void testGetStats_TauxAcceptationZero() {
        // Given
        when(candidatureClient.getTotalCandidatures()).thenReturn(0);
        when(candidatureClient.getAcceptedCandidatures()).thenReturn(0);
        when(authClient.getTotalCandidates()).thenReturn(0);
        when(interviewClient.getTotalInterviews()).thenReturn(0);
        when(candidatureClient.getOffresOuvertesCount()).thenReturn(0);
        when(candidatureClient.getOffresFermeesCount()).thenReturn(0);
        when(notificationClient.getTotalNotifications()).thenReturn(0);

        // When
        DashboardStats stats = dashboardService.getStats();

        // Then
        assertEquals(0.0, stats.getTauxAcceptation());
    }

    @Test
    @DisplayName("getStats - Gestion des erreurs des services distants")
    void testGetStats_HandleRemoteServiceErrors() {
        // Given - Simulation d'erreurs sur certains services
        when(candidatureClient.getTotalCandidatures()).thenThrow(new RuntimeException("Service indisponible"));
        when(candidatureClient.getAcceptedCandidatures()).thenReturn(40);
        when(authClient.getTotalCandidates()).thenReturn(25);
        when(interviewClient.getTotalInterviews()).thenReturn(15);
        when(candidatureClient.getOffresOuvertesCount()).thenReturn(10);
        when(candidatureClient.getOffresFermeesCount()).thenReturn(5);
        when(notificationClient.getTotalNotifications()).thenReturn(80);

        // When
        DashboardStats stats = dashboardService.getStats();

        // Then - Les valeurs par défaut (0) sont utilisées pour les services en erreur
        assertNotNull(stats);
        assertEquals(0, stats.getTotalCandidatures()); // Valeur par défaut
        assertEquals(40, stats.getCandidaturesAcceptees());
        assertEquals(25, stats.getCandidaturesNouvelles());
        assertEquals(15, stats.getEntretiensPlanifies());
        assertEquals(10, stats.getOffresOuvertes());
        assertEquals(5, stats.getOffresFermees());
        assertEquals(80, stats.getNotificationsEnvoyees());
    }

    @Test
    @DisplayName("getStats - Tous les services sont appelés en parallèle")
    void testGetStats_ParallelCalls() {
        // Given
        when(candidatureClient.getTotalCandidatures()).thenAnswer(invocation -> {
            Thread.sleep(100);
            return 100;
        });
        when(candidatureClient.getAcceptedCandidatures()).thenAnswer(invocation -> {
            Thread.sleep(100);
            return 40;
        });
        when(authClient.getTotalCandidates()).thenAnswer(invocation -> {
            Thread.sleep(100);
            return 25;
        });
        when(interviewClient.getTotalInterviews()).thenAnswer(invocation -> {
            Thread.sleep(100);
            return 15;
        });
        when(candidatureClient.getOffresOuvertesCount()).thenReturn(10);
        when(candidatureClient.getOffresFermeesCount()).thenReturn(5);
        when(notificationClient.getTotalNotifications()).thenReturn(80);

        // When
        long startTime = System.currentTimeMillis();
        DashboardStats stats = dashboardService.getStats();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Les appels parallèles devraient prendre moins de 500ms
        assertNotNull(stats);
        assertTrue(duration < 500, "Les appels parallèles devraient être rapides: " + duration + "ms");
    }
}