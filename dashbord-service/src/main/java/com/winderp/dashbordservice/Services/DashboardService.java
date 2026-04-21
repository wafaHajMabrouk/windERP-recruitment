package com.winderp.dashbordservice.Services;

import com.winderp.dashbordservice.Client.AuthClient;
import com.winderp.dashbordservice.Client.CandidatureClient;
import com.winderp.dashbordservice.Client.InterviewClient;
import com.winderp.dashbordservice.Client.NotificationClient;
import com.winderp.dashbordservice.Models.DashboardStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AuthClient authClient;
    private final CandidatureClient candidatureClient;
    private final InterviewClient interviewClient;
    private final NotificationClient notificationClient;
              // ← NOUVEAU

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        stats.setTotalCandidatures(safeCall(candidatureClient::getTotalCandidatures, 0));
        stats.setCandidaturesNouvelles(safeCall(authClient::getTotalCandidates, 0));
        stats.setCandidaturesAcceptees(safeCall(candidatureClient::getAcceptedCandidatures, 0));   // ← NOUVEAU
        stats.setEntretiensPlanifies(safeCall(interviewClient::getTotalInterviews, 0));
        stats.setOffresOuvertes(safeCall(candidatureClient::getOffresOuvertesCount, 0));               // ← NOUVEAU
        stats.setOffresFermees(safeCall(candidatureClient::getOffresFermeesCount, 0));                 // ← NOUVEAU
        stats.setNotificationsEnvoyees(safeCall(notificationClient::getTotalNotifications, 0));

        return stats;
    }

    private <T> T safeCall(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel distant : {}", e.getMessage());
            return defaultValue;
        }
    }
}