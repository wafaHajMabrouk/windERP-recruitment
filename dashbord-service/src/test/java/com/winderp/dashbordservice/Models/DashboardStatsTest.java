package com.winderp.dashbordservice.Models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardStatsTest {

    private DashboardStats dashboardStats;

    @BeforeEach
    void setUp() {
        dashboardStats = new DashboardStats();
    }

    @Test
    @DisplayName("Test setters et getters")
    void testSettersAndGetters() {
        // Given
        dashboardStats.setTotalCandidatures(100);
        dashboardStats.setCandidaturesNouvelles(25);
        dashboardStats.setCandidaturesAcceptees(40);
        dashboardStats.setEntretiensPlanifies(15);
        dashboardStats.setOffresOuvertes(10);
        dashboardStats.setOffresFermees(5);
        dashboardStats.setNotificationsEnvoyees(80);
        dashboardStats.setTauxAcceptation(40.0);
        dashboardStats.setScoreMoyen(85.5);

        // Then
        assertEquals(100, dashboardStats.getTotalCandidatures());
        assertEquals(25, dashboardStats.getCandidaturesNouvelles());
        assertEquals(40, dashboardStats.getCandidaturesAcceptees());
        assertEquals(15, dashboardStats.getEntretiensPlanifies());
        assertEquals(10, dashboardStats.getOffresOuvertes());
        assertEquals(5, dashboardStats.getOffresFermees());
        assertEquals(80, dashboardStats.getNotificationsEnvoyees());
        assertEquals(40.0, dashboardStats.getTauxAcceptation());
        assertEquals(85.5, dashboardStats.getScoreMoyen());
    }

    @Test
    @DisplayName("Test valeurs par défaut")
    void testDefaultValues() {
        assertEquals(0, dashboardStats.getTotalCandidatures());
        assertEquals(0, dashboardStats.getCandidaturesNouvelles());
        assertEquals(0, dashboardStats.getCandidaturesAcceptees());
        assertEquals(0.0, dashboardStats.getTauxAcceptation());
        assertEquals(0.0, dashboardStats.getScoreMoyen());
    }
}