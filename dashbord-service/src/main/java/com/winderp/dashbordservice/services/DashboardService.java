package com.winderp.dashbordservice.services;

import com.winderp.dashbordservice.client.*;
import com.winderp.dashbordservice.models.DashboardStats;
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


    public DashboardStats getStats() {

        DashboardStats stats = new DashboardStats();


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



        CompletableFuture.allOf(
                totalCandidatures, accepted, newCandidates

        ).join();


        stats.setTotalCandidatures(totalCandidatures.join());
        stats.setCandidaturesAcceptees(accepted.join());
        stats.setCandidaturesNouvelles(newCandidates.join());
        stats.setEntretiensPlanifies(interviews.join());
        stats.setOffresOuvertes(offresOuvertes.join());
        stats.setOffresFermees(offresFermees.join());


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