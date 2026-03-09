package com.winderp.dashbordservice.Services;

import com.winderp.dashbordservice.Client.AuthClient;
import com.winderp.dashbordservice.Client.CandidatureClient;
import com.winderp.dashbordservice.Client.InterviewClient;
import com.winderp.dashbordservice.Client.NotificationClient;
import com.winderp.dashbordservice.Models.DashboardStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AuthClient authClient;
    private final CandidatureClient candidatureClient;
    private final InterviewClient interviewClient;
    private final NotificationClient notificationClient;

    // Méthode pour calculer toutes les statistiques du dashboard
    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        // Nombre total de candidatures
        stats.setTotalCandidatures(candidatureClient.getTotalCandidatures());

        // Nombre total de candidats
        stats.setCandidaturesNouvelles(authClient.getTotalCandidates());

        // Nombre d'entretiens planifiés
        stats.setEntretiensPlanifies(interviewClient.getTotalInterviews());

        // Nombre de notifications envoyées
        stats.setNotificationsEnvoyees(notificationClient.getTotalNotifications());

        return stats;
    }
}