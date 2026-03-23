package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.SERVICE.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;

    @PostMapping
    public Offre create(@RequestBody Offre offre) {
        return offreService.create(offre);
    }

    @GetMapping
    public List<Offre> getAll() {
        return offreService.getAll();
    }
}