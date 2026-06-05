package com.winderp.dashbordservice.controllers;

import com.winderp.dashbordservice.models.DashboardStats;
import com.winderp.dashbordservice.models.Rapport;
import com.winderp.dashbordservice.services.DashboardService;
import com.winderp.dashbordservice.services.RapportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private RapportService rapportService;

    @Autowired
    private ObjectMapper objectMapper;

    private DashboardStats dashboardStats;
    private Rapport rapport;
    private List<Rapport> rapportList;

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, Month.JANUARY, 15);

    @BeforeEach
    void setUp() {
        dashboardStats = new DashboardStats();
        dashboardStats.setTotalCandidatures(100);
        dashboardStats.setCandidaturesNouvelles(25);
        dashboardStats.setCandidaturesAcceptees(40);
        dashboardStats.setEntretiensPlanifies(15);
        dashboardStats.setOffresOuvertes(10);
        dashboardStats.setOffresFermees(5);

        dashboardStats.setTauxAcceptation(40.0);
        dashboardStats.setScoreMoyen(85.5);

        rapport = new Rapport();
        rapport.setId(1L);
        rapport.setTitre("Rapport Mensuel");
        rapport.setDescription("Rapport du mois de Mai");
        rapport.setDateCreation(FIXED_DATE);

        Rapport rapport2 = new Rapport();
        rapport2.setId(2L);
        rapport2.setTitre("Rapport Hebdomadaire");
        rapport2.setDescription("Rapport de la semaine");
        rapport2.setDateCreation(FIXED_DATE);

        rapportList = Arrays.asList(rapport, rapport2);
    }

    // ... (tous les tests restent identiques)
    // (les méthodes de test sont inchangées, seul le setUp est modifié)
    // Pour gagner de la place, on garde les mêmes tests que précédemment.
    // Assurez-vous de recopier l'intégralité des méthodes de test de votre version actuelle.
}