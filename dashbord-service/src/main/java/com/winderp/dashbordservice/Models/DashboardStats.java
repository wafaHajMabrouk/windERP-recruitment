package com.winderp.dashbordservice.Models;

import lombok.Data;

@Data
public class DashboardStats {

    private long totalCandidatures;
    private long candidaturesNouvelles;
    private long candidaturesAcceptees;
    private long entretiensPlanifies;
    private long offresOuvertes;
    private long offresFermees;
    private long notificationsEnvoyees;
    // 🔥 KPI BONUS PFE
    private double tauxAcceptation;
    private double scoreMoyen;
}