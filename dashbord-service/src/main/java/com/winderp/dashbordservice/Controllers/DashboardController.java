package com.winderp.dashbordservice.Controllers;

import com.winderp.dashbordservice.Models.DashboardStats;
import com.winderp.dashbordservice.Models.Rapport;
import com.winderp.dashbordservice.Services.DashboardService;
import com.winderp.dashbordservice.Services.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final RapportService rapportService;

    // Endpoint pour récupérer les statistiques du dashboard
    @GetMapping("/stats")
    public DashboardStats getStats(){
        return dashboardService.getStats();
    }

    // Endpoint pour créer un rapport
    @PostMapping("/rapports")
    public Rapport createRapport(@RequestBody Rapport rapport){
        return rapportService.createRapport(rapport);
    }

    // Endpoint pour récupérer tous les rapports
    @GetMapping("/rapports")
    public List<Rapport> getRapports(){
        return rapportService.getAllRapports();
    }

    // Endpoint pour supprimer un rapport
    @DeleteMapping("/rapports/{id}")
    public void deleteRapport(@PathVariable Long id){
        rapportService.deleteRapport(id);
    }
}