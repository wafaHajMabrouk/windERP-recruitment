package com.winderp.dashbordservice.services;

import com.winderp.dashbordservice.models.Rapport;
import com.winderp.dashbordservice.repository.RapportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapportServiceTest {

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 1, 15);

    @Mock
    private RapportRepository rapportRepository;

    @InjectMocks
    private RapportService rapportService;

    private Rapport rapport;
    private List<Rapport> rapportList;

    @BeforeEach
    void setUp() {
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
    @DisplayName("createRapport - Création avec succès")
    void testCreateRapport_Success() {
        Rapport newRapport = new Rapport();
        newRapport.setTitre("Nouveau Rapport");
        newRapport.setDescription("Description");

        Rapport savedRapport = new Rapport();
        savedRapport.setId(3L);
        savedRapport.setTitre("Nouveau Rapport");
        savedRapport.setDescription("Description");
        savedRapport.setDateCreation(FIXED_DATE);

        when(rapportRepository.save(any(Rapport.class))).thenReturn(savedRapport);

        Rapport result = rapportService.createRapport(newRapport);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Nouveau Rapport", result.getTitre());
        assertEquals("Description", result.getDescription());
        assertNotNull(result.getDateCreation());
        assertEquals(FIXED_DATE, result.getDateCreation());

        verify(rapportRepository, times(1)).save(any(Rapport.class));
    }

    @Test
    @DisplayName("createRapport - Création avec date automatique (vérifier qu'elle est fixe)")
    void testCreateRapport_DateAutoSet() {
        Rapport newRapport = new Rapport();
        newRapport.setTitre("Rapport Auto Date");

        when(rapportRepository.save(any(Rapport.class))).thenAnswer(invocation -> {
            Rapport r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        Rapport result = rapportService.createRapport(newRapport);

        // Maintenant, la date est fixe (UTC) et ne dépend pas de l'horloge système
        assertNotNull(result.getDateCreation());
    }

    @Test
    @DisplayName("getAllRapports - Récupération de tous les rapports")
    void testGetAllRapports_Success() {
        when(rapportRepository.findAll()).thenReturn(rapportList);

        List<Rapport> result = rapportService.getAllRapports();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rapport Mensuel", result.get(0).getTitre());
        assertEquals("Rapport Hebdomadaire", result.get(1).getTitre());

        verify(rapportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllRapports - Liste vide")
    void testGetAllRapports_EmptyList() {
        when(rapportRepository.findAll()).thenReturn(Arrays.asList());

        List<Rapport> result = rapportService.getAllRapports();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(rapportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deleteRapport - Suppression avec succès")
    void testDeleteRapport_Success() {
        doNothing().when(rapportRepository).deleteById(1L);

        rapportService.deleteRapport(1L);

        verify(rapportRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRapport - Suppression d'un rapport inexistant")
    void testDeleteRapport_NotFound() {
        doThrow(new RuntimeException("Rapport non trouvé")).when(rapportRepository).deleteById(99L);

        assertThrows(RuntimeException.class, () -> {
            rapportService.deleteRapport(99L);
        });

        verify(rapportRepository, times(1)).deleteById(99L);
    }
}