package com.winderp.dashbordservice.controllers;

import com.winderp.dashbordservice.models.DashboardStats;
import com.winderp.dashbordservice.models.Rapport;
import com.winderp.dashbordservice.services.DashboardService;
import com.winderp.dashbordservice.services.RapportService;
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


    @GetMapping("/stats")
    public DashboardStats getStats(){
        return dashboardService.getStats();
    }


    @PostMapping("/rapports")
    public Rapport createRapport(@RequestBody Rapport rapport){
        return rapportService.createRapport(rapport);
    }


    @GetMapping("/rapports")
    public List<Rapport> getRapports(){
        return rapportService.getAllRapports();
    }


    @DeleteMapping("/rapports/{id}")
    public void deleteRapport(@PathVariable Long id){
        rapportService.deleteRapport(id);
    }
}
