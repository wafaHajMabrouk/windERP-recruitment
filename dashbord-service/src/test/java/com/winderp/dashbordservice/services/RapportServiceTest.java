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
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapportServiceTest {

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, Month.JANUARY, 15);

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

    // ... (tous les tests inchangés)
}