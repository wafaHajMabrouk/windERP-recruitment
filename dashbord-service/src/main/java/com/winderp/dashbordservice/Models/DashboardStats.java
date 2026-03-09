package com.winderp.dashbordservice.Models;

import lombok.Data;

@Data
public class DashboardStats {

    private int totalCandidatures;
    private int candidaturesNouvelles;
    private int entretiensPlanifies;
    private int notificationsEnvoyees;

}