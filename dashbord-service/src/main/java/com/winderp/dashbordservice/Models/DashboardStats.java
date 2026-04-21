package com.winderp.dashbordservice.Models;

import lombok.Data;

@Data
public class DashboardStats {

    private int totalCandidatures;
    private int candidaturesNouvelles;
    private int candidaturesAcceptees;     // ← NOUVEAU
    private int entretiensPlanifies;
    private int offresOuvertes;            // ← NOUVEAU
    private int offresFermees;             // ← NOUVEAU
    private int notificationsEnvoyees;
}