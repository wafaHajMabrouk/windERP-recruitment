package com.winderp.dashbordservice.Services;

import com.winderp.dashbordservice.Client.*;
import com.winderp.dashbordservice.Models.DashboardStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AuthClient authClient;
    private final CandidatureClient candidatureClient;
    private final InterviewClient interviewClient;
    private final NotificationClient notificationClient;

    public DashboardStats getStats() {

        DashboardStats stats = new DashboardStats();

        // 🔥 Appels parallèles
        CompletableFuture<Integer> totalCandidatures =
                CompletableFuture.supplyAsync(() -> safe(() -> candidatureClient.getTotalCandidatures(), 0));

        CompletableFuture<Integer> accepted =
                CompletableFuture.supplyAsync(() -> safe(() -> candidatureClient.getAcceptedCandidatures(), 0));

        CompletableFuture<Integer> newCandidates =
                CompletableFuture.supplyAsync(() -> safe(() -> authClient.getTotalCandidates(), 0));

        CompletableFuture<Integer> interviews =
                CompletableFuture.supplyAsync(() -> safe(() -> interviewClient.getTotalInterviews(), 0));

        CompletableFuture<Integer> offresOuvertes =
                CompletableFuture.supplyAsync(() -> safe(() -> candidatureClient.getOffresOuvertesCount(), 0));

        CompletableFuture<Integer> offresFermees =
                CompletableFuture.supplyAsync(() -> safe(() -> candidatureClient.getOffresFermeesCount(), 0));

        CompletableFuture<Integer> notifications =
                CompletableFuture.supplyAsync(() -> safe(() -> notificationClient.getTotalNotifications(), 0));

        CompletableFuture.allOf(
                totalCandidatures, accepted, newCandidates,
                interviews, offresOuvertes, offresFermees, notifications
        ).join();

        // 🔹 Set values
        stats.setTotalCandidatures(totalCandidatures.join());
        stats.setCandidaturesAcceptees(accepted.join());
        stats.setCandidaturesNouvelles(newCandidates.join());
        stats.setEntretiensPlanifies(interviews.join());
        stats.setOffresOuvertes(offresOuvertes.join());
        stats.setOffresFermees(offresFermees.join());
        stats.setNotificationsEnvoyees(notifications.join());

        // 🔥 KPI intelligents
        if (stats.getTotalCandidatures() > 0) {
            stats.setTauxAcceptation(
                    (double) stats.getCandidaturesAcceptees()
                            / stats.getTotalCandidatures() * 100
            );
        } else {
            stats.setTauxAcceptation(0);
        }

        return stats;
    }

    private <T> T safe(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("Erreur service distant: {}", e.getMessage());
            return defaultValue;
        }
    }

}