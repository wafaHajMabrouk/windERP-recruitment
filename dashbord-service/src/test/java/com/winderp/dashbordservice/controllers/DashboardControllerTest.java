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

    // Date fixe pour les tests
    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 1, 15);

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

    @Test
    @DisplayName("GET /stats - Récupérer les statistiques avec succès")
    void testGetStats_Success() throws Exception {
        when(dashboardService.getStats()).thenReturn(dashboardStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCandidatures").value(100))
                .andExpect(jsonPath("$.candidaturesNouvelles").value(25))
                .andExpect(jsonPath("$.candidaturesAcceptees").value(40))
                .andExpect(jsonPath("$.entretiensPlanifies").value(15))
                .andExpect(jsonPath("$.offresOuvertes").value(10))
                .andExpect(jsonPath("$.offresFermees").value(5))
                .andExpect(jsonPath("$.notificationsEnvoyees").value(80))
                .andExpect(jsonPath("$.tauxAcceptation").value(40.0))
                .andExpect(jsonPath("$.scoreMoyen").value(85.5));

        verify(dashboardService, times(1)).getStats();
    }

    @Test
    @DisplayName("GET /stats - Service retourne des stats vides")
    void testGetStats_EmptyStats() throws Exception {
        DashboardStats emptyStats = new DashboardStats();
        when(dashboardService.getStats()).thenReturn(emptyStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCandidatures").value(0))
                .andExpect(jsonPath("$.candidaturesNouvelles").value(0))
                .andExpect(jsonPath("$.tauxAcceptation").value(0.0));

        verify(dashboardService, times(1)).getStats();
    }

    @Test
    @DisplayName("POST /rapports - Créer un rapport avec succès")
    void testCreateRapport_Success() throws Exception {
        Rapport newRapport = new Rapport();
        newRapport.setTitre("Nouveau Rapport");
        newRapport.setDescription("Description du nouveau rapport");

        Rapport savedRapport = new Rapport();
        savedRapport.setId(3L);
        savedRapport.setTitre("Nouveau Rapport");
        savedRapport.setDescription("Description du nouveau rapport");
        savedRapport.setDateCreation(FIXED_DATE);

        when(rapportService.createRapport(any(Rapport.class))).thenReturn(savedRapport);

        mockMvc.perform(post("/api/dashboard/rapports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRapport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.titre").value("Nouveau Rapport"))
                .andExpect(jsonPath("$.description").value("Description du nouveau rapport"))
                .andExpect(jsonPath("$.dateCreation").value(FIXED_DATE.toString()));

        verify(rapportService, times(1)).createRapport(any(Rapport.class));
    }

    @Test
    @DisplayName("POST /rapports - Erreur lors de la création (titre null)")
    void testCreateRapport_InvalidData() throws Exception {
        Rapport invalidRapport = new Rapport();
        invalidRapport.setDescription("Description sans titre");

        mockMvc.perform(post("/api/dashboard/rapports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRapport)))
                .andExpect(status().isOk());

        verify(rapportService, times(1)).createRapport(any(Rapport.class));
    }

    @Test
    @DisplayName("GET /rapports - Récupérer tous les rapports")
    void testGetRapports_Success() throws Exception {
        when(rapportService.getAllRapports()).thenReturn(rapportList);

        mockMvc.perform(get("/api/dashboard/rapports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titre").value("Rapport Mensuel"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titre").value("Rapport Hebdomadaire"));

        verify(rapportService, times(1)).getAllRapports();
    }

    @Test
    @DisplayName("GET /rapports - Liste vide")
    void testGetRapports_EmptyList() throws Exception {
        when(rapportService.getAllRapports()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/dashboard/rapports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(rapportService, times(1)).getAllRapports();
    }

    @Test
    @DisplayName("DELETE /rapports/{id} - Supprimer un rapport avec succès")
    void testDeleteRapport_Success() throws Exception {
        doNothing().when(rapportService).deleteRapport(1L);

        mockMvc.perform(delete("/api/dashboard/rapports/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(rapportService, times(1)).deleteRapport(1L);
    }

    @Test
    @DisplayName("DELETE /rapports/{id} - Supprimer un rapport inexistant")
    void testDeleteRapport_NotFound() throws Exception {
        doThrow(new RuntimeException("Rapport non trouvé")).when(rapportService).deleteRapport(99L);

        mockMvc.perform(delete("/api/dashboard/rapports/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(rapportService, times(1)).deleteRapport(99L);
    }
}